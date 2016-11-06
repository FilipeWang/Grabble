package com.filipewang.grabble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

public class LeaderboardScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static int RC_SIGN_IN = 9001;
    private static int LEADERBOARD = 1000;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false;
    private String TAG = "LeaderboardScreen";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_screen);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .setViewForPopups(findViewById(android.R.id.content))
                // add other APIs and scopes here as needed
                .build();

        Button buttonLeaderboard = (Button) findViewById(R.id.buttonShowLeader);
        SignInButton buttonSignin = (SignInButton) findViewById(R.id.buttonSignin2);
        Button buttonSignout = (Button) findViewById(R.id.buttonSignout);

        buttonLeaderboard.setOnClickListener(this);
        buttonSignin.setOnClickListener(this);
        buttonSignout.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "Error")) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSignout) {
            // user explicitly signed out, so turn off auto sign in
            mExplicitSignOut = true;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
            // show sign-in button, hide the sign-out button
            findViewById(R.id.buttonSignin2).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonSignout).setVisibility(View.GONE);
        } else if (view.getId() == R.id.buttonShowLeader){
            FileManager fm = new FileManager();
            int [] letters = fm.retrieveLetters();
            int sum = 0;
            for (int letter : letters) {
                sum = sum + letter;
            }
            Log.d(TAG,"Sum: " + sum);
            Games.Leaderboards.submitScore(mGoogleApiClient, getApplicationContext().getResources().getString(R.string.leaderboard_grabble), sum);
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    getApplicationContext().getResources().getString(R.string.leaderboard_grabble)), LEADERBOARD);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // show sign-out button, hide the sign-in button
        findViewById(R.id.buttonSignin2).setVisibility(View.GONE);
        findViewById(R.id.buttonSignout).setVisibility(View.VISIBLE);

        // (your code here: update UI, enable functionality that depends on sign in, etc)
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        } else if(requestCode == LEADERBOARD){
            if(intent != null)
                startActivity(intent);
        }
        super.onActivityResult(requestCode,resultCode,intent);
    }

}

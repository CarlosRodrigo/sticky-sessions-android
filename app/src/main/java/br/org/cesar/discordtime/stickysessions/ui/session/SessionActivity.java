package br.org.cesar.discordtime.stickysessions.ui.session;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import br.org.cesar.discordtime.stickysessions.R;
import br.org.cesar.discordtime.stickysessions.app.StickySessionApplication;
import br.org.cesar.discordtime.stickysessions.presentation.notes.INoteTopicDetail;
import br.org.cesar.discordtime.stickysessions.presentation.session.SessionContract;
import br.org.cesar.discordtime.stickysessions.ui.ExtraNames;
import br.org.cesar.discordtime.stickysessions.ui.adapters.TopicSessionNotesAdapter;
import br.org.cesar.discordtime.stickysessions.ui.session.custom.ItemAnimator;

public class SessionActivity extends AppCompatActivity implements SessionContract.View {
    private final static String TAG = "SessionActivity";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mToolbarTitleTextView;
    private Context mContext;

    private TopicSessionNotesAdapter mTopicSessionNotesAdapter;

    @Inject
    SessionContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        ((StickySessionApplication) getApplicationContext()).inject(this);
        mContext = this;

        bindView();
        configureSession();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        configureSession();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    /* SessionContract.View overrides --- */

    @Override
    public void displayError(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayTitle(String title) {
        if (mToolbarTitleTextView != null) {
            mToolbarTitleTextView.setText(title);
        }
    }

    @Override
    public void shareSession(String sessionId){
        Intent sendIntent=new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
        String.format(getString(R.string.share_session),sessionId));
        startActivity(sendIntent);
    }

    @Override
    public void displaySession(@NotNull List<? extends INoteTopicDetail> sessionDetail) {
        if (mTopicSessionNotesAdapter != null) {
            mTopicSessionNotesAdapter.replaceData(sessionDetail);
        }
    }

    private void bindView() {
        configureToolbar();

        mRecyclerView = findViewById(R.id.user_topic_session);
        mProgressBar = findViewById(R.id.progress_bar);

        initializeTopicSessionList();
        mPresenter.attachView(this);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbarTitleTextView = findViewById(R.id.toolbar_title);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initializeTopicSessionList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator(this));

        mTopicSessionNotesAdapter = new TopicSessionNotesAdapter();
        mRecyclerView.setAdapter(mTopicSessionNotesAdapter);
    }

    private void configureSession() {
        Intent intent = getIntent();
        //Enter in a session by link
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String sessionId = uri.getQueryParameter(ExtraNames.SESSION_ID);
                Log.d(TAG, "sessionId " + sessionId);
                mPresenter.currentSession(sessionId);
            } else {
                //TODO error message to null data
                Log.d(TAG, "null sessionId.");
            }
        //Enter in a session by Lobby
        } else if(!TextUtils.isEmpty(intent.getStringExtra(ExtraNames.SESSION_ID))) {
            String sessionId = intent.getStringExtra(ExtraNames.SESSION_ID);
            Log.d(TAG, "sessionId " + sessionId);
            mPresenter.currentSession(sessionId);
        }
    }
}

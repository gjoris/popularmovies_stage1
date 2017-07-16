package eu.droidit.nanodegree.android.popularmovies.stage1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.droidit.nanodegree.android.popularmovies.stage1.adapter.ImageAdapter;
import eu.droidit.nanodegree.android.popularmovies.stage1.settings.API;
import eu.droidit.nanodegree.android.popularmovies.stage1.settings.APIType;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.gridview)
    GridView gridView;
    int lastSelectedItem = R.id.popular;
    ImageAdapter adapter;
    @BindView(R.id.main_loading_indicator)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //read the key at startup
        API.readKey(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(APIType.basedOnId(lastSelectedItem).getDescription().concat(" Movies"));
        adapter = new ImageAdapter(
                this,
                APIType.basedOnId(lastSelectedItem),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                },
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
        );
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetail.class);
                intent.putExtra(Intent.EXTRA_TEXT, position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.type_menu, menu);
        if (lastSelectedItem == R.id.popular) {
            menu.findItem(R.id.popular).setVisible(false);
        } else {
            menu.findItem(R.id.top_rated).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.lastSelectedItem = item.getItemId();
        invalidateOptionsMenu();
        adapter.reload(APIType.basedOnId(lastSelectedItem));
        setTitle(APIType.basedOnId(lastSelectedItem).getDescription().concat(" Movies"));
        return true;
    }
}

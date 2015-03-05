package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.adapters.MessageArrayAdapter;

public class MessageActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_lower_level, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((TextView) abv.findViewById(R.id.lower_level_title)).setText("Swag Lord");

        String temp[] = new String[]{"hello fellow gamer j;;as ad d a s s a s as d as d", "sad f asd fa sdf asdf ads fa s fa ds",
                "fa dsf asd fad u u u ", "u  tyu", " nt ynu t yun ty", "untynut;lkynutynut yun ty un ty ",
                "nut yu nty nu tyu nt yu ntyun t yun ty nu", "  n n n jtyntyjnt s f asd fa dsf asd fads fa sdf as df s",
                "message", "message", "end"};
        setListAdapter(new MessageArrayAdapter(this, temp));
        getListView().setSelection(getListAdapter().getCount() - 1);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_message, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void goBack(View view) {
        finish();
    }

    public void sendMessage(View view) {
        // send message in the edit text
        Toast.makeText(this, "Sending message...", Toast.LENGTH_SHORT).show();
    }
}

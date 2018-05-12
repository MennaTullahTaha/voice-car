package com.android.team.ble;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class CommandsActivity extends AppCompatActivity {
  RecyclerView recyclerView;
    FirebaseDatabase database ;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("/commands/");

        ArrayList<Command> newCommands=
                (ArrayList<Command>) getIntent().getExtras().get("newCommands");
        for (Command c:newCommands) {
            myRef.push().setValue(c);
        }
        newCommands.clear();

        final FirebaseRecyclerAdapter<Command,CommandViewHolder> adapter = new FirebaseRecyclerAdapter<Command,CommandViewHolder>(
                Command.class,
                R.layout.command_view,
                CommandViewHolder.class,
                myRef
        ) {
            @Override
            protected void populateViewHolder(CommandViewHolder viewHolder, Command model, final int position) {
                viewHolder.direction.setText(model.getCommandDirection());
                viewHolder.distance.setText(model.getCommandDistance());

           }

            @Override
            public void onBindViewHolder(final CommandViewHolder holder, final int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRef(position).removeValue();
                    }
                });
                holder.direction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String direction=holder.direction.getText().toString();
                        String distance= holder.distance.getText().toString();
                        Intent intent =new Intent();
                        intent.putExtra("command",new Command(direction,distance));
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

}


}

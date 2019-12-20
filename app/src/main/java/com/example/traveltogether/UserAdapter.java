package com.example.traveltogether;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    List<User> listUser;
    String theLastMsg;
    boolean isChat;
    StorageReference storageRef;


    public UserAdapter(Context mContext, List<User> mUser, boolean isChat) {
        this.context = mContext;
        this.listUser = mUser;
        this.isChat = isChat;
        storageRef = FirebaseStorage.getInstance().getReference();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = listUser.get(position);
        holder.userName.setText(user.getHoTen());

        if (!user.getSrc().equals(""))
            Glide.with(context).load(user.getSrc()).into(holder.userImage);
        else holder.userImage.setImageResource(R.drawable.null_avata);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getUid());
                context.startActivity(intent);
            }
        });

        if(isChat)
        {
            lastMessage(user.getUid(),holder.lastMsg);
        }
        else
            holder.lastMsg.setVisibility(View.GONE);

        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public CircleImageView userImage;
        public CircleImageView img_on;
        public CircleImageView img_off;
        public TextView lastMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.userImage);
            img_on = itemView.findViewById(R.id.online);
            img_off = itemView.findViewById(R.id.offline);
            lastMsg = itemView.findViewById(R.id.lastMsg);
        }
    }

    private void lastMessage(final String userid, final TextView lastMsg) {
        theLastMsg = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
        mData.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) || chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid)) {
                        theLastMsg = chat.getMsg();
                    }
                }
                switch (theLastMsg) {
                    case "":
                        lastMsg.setText("");
                        break;
                    default:
                        lastMsg.setText(theLastMsg);
                        break;
                }
                theLastMsg = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.example.traveltogether;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    static final int MSG_LEFT = 0;
    static final int MSG_RIGHT = 1;
    static final int IMG_LEFT = 2;
    static final int IMG_RIGHT = 3;
    static final int VID_LEFT = 4;
    static final int VID_RIGHT = 5;
    MediaController controller;
    Context context;
    List<Chat> chatList;
    StorageReference storageRef;
    FirebaseUser firebaseUser;
    String imgUrl;



    public MessageAdapter(Context mContext, List<Chat> chatList, String imgUrl) {
        this.context = mContext;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
        storageRef = FirebaseStorage.getInstance().getReference();

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType == MSG_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType == IMG_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_img_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType == IMG_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_img_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType == VID_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_vid_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_vid_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        Chat chat = chatList.get(position);
        if (!chat.getType().equals("img") && !chat.getType().equals("vid"))
            holder.show_msg.setText(chat.getMsg());
        if (!imgUrl.equals(""))
            Glide.with(context).load(imgUrl).into(holder.userImage);
        else
            holder.userImage.setImageResource(R.drawable.null_avata);

        if (position == chatList.size() - 1) {

            if (chat.getIsSeen().equals("true")) {
                if (chat.getSender().equals(firebaseUser.getUid()))
                    holder.txtIsSeen.setText("Đã xem");
                else holder.txtIsSeen.setVisibility(View.GONE);
            } else {
                holder.txtIsSeen.setText("Đã gửi");
            }

        } else
            holder.txtIsSeen.setVisibility(View.GONE);

        if (chat.getType().equals("img"))
            Glide.with(context).load(chat.msg).into(holder.img);

        if (chat.getType().equals("vid")) {
            holder.videoView.setVideoPath(chat.getMsg());
            final MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.video_layout);
            holder.videoView.setMediaController(mediaController);
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    holder.videoView.seekTo(0);
                    mediaController.setAnchorView(holder.video_layout);

                }
            });


        }

//        if (chat.getType().equals("vid")) {
//            holder.mVideoView.setVideoPath(chat.getMsg());
//            holder.mVideoView.setMediaController(holder.mMediaController);
//            holder.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    holder.mVideoView.seekTo(0);
//                }
//            });
//
//        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_msg;
        public CircleImageView userImage;
        public TextView txtIsSeen;
        public ImageView img;
        public VideoView videoView;
        public FrameLayout video_layout;
//        public UniversalMediaController mMediaController;
//        public UniversalVideoView mVideoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_msg = itemView.findViewById(R.id.show_msg);
            userImage = itemView.findViewById(R.id.userImage);
            txtIsSeen = itemView.findViewById(R.id.isseen);
            img = itemView.findViewById(R.id.img);
            videoView = itemView.findViewById(R.id.vid);
            video_layout= itemView.findViewById(R.id.video_layout);
//            mVideoView = itemView.findViewById(R.id.videoView);
//            mMediaController =  itemView.findViewById(R.id.media_controller);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getType().equals("msg")) {
            if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
                return MSG_RIGHT;
            else return MSG_LEFT;
        } else if (chatList.get(position).getType().equals("img")) {
            if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
                return IMG_RIGHT;
            else return IMG_LEFT;
        } else if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
            return VID_RIGHT;
        else return VID_LEFT;
    }


}
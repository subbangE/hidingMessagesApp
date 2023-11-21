package team.everywhere.chatapp2023;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Chat> mDataset;

    private long lastClickTime = 0;

    public ArrayList<Integer> see_position = new ArrayList<Integer>();
    String stMyEmail = "";

    int position;
    private RecyclerView mRecyclerView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView time_textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.tvChat);
            time_textView = v.findViewById(R.id.time_tvChat);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 1000) { // 1초 동안 중복 클릭 무시
                        return true;
                    }
                    lastClickTime = currentTime;

                    int position = getAdapterPosition();

                    if(mDataset.get(position).getCheck_hide().equals("yes")) { //숨김 메시지 일 경우에만 실행
                        Context context = itemView.getContext();
                        Intent intent = new Intent(context, PasswdActivity.class);

                        intent.putExtra("position", position);
                        ((Activity) context).startActivityForResult(intent, 2);

                        return true;
                    }else{
                        return true;
                    }
                }
            });
        }
    }



    @Override   // chatbubble 상대방이 보낸 것과 내가 보낸 것 구분
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (mDataset.get(position) != null && "no".equals(mDataset.get(position).check_hide)) {
            if (mDataset.get(position).email.equals(stMyEmail)) {
                return 1;
            } else {
                return 2;
            }
        } else {
            if(see_position.contains(mDataset.get(position))) {
                if (mDataset.get(position).email.equals(stMyEmail))
                    return 1;
                else
                    return 2;
            }else{
                if (mDataset.get(position).email.equals(stMyEmail))
                    return 3;
                else
                    return 4;
            }
        }
    }

    public MyAdapter(ArrayList<Chat> myDataset, String stEmail) {
        mDataset = myDataset;
        this.stMyEmail = stEmail;
    }

    @Override // dataset 구성
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        if (viewType == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_text_view, parent, false);
        } else if (viewType == 3) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_space_text, parent, false);
        } else if (viewType == 4) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_space_text, parent, false);
        }

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (mDataset.get(position) != null && "no".equals(mDataset.get(position).check_hide)) {
            holder.textView.setText(mDataset.get(position).getText());
            holder.time_textView.setText((mDataset.get(position).getChat_time()));
        } else{
            holder.textView.setText("숨겨진 메세지입니다.");
            holder.time_textView.setText((mDataset.get(position).getChat_time()));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



}
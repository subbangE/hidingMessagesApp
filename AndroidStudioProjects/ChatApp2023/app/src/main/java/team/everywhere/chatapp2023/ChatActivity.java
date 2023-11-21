package team.everywhere.chatapp2023;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    MyAdapter mAdapter; //MyAdapter로 선언
    private RecyclerView.LayoutManager layoutManager;
    private long lastClickTime = 0;
    EditText etText;    // 채팅방 입력 부분
    Button btnSend;     // 채팅 입력 후 전송 버튼
    String stEmail;

    String stcheck_hide="no"; // 기본적으로 숨김 옵션 off

    String stcheck_hide_result; // 숨김시 비밀번호 확인

    CheckBox hide_CheckBox; //숨김 스위치
    FirebaseDatabase database;
    ArrayList<Chat> chatArrayList; // 이메일과 채팅 입력 텍스트를 배열하는 리스트
    int chat_position; //잠금 메시지 해제 위치

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        database = FirebaseDatabase.getInstance();

        chatArrayList = new ArrayList<>();
        stEmail = getIntent().getStringExtra("email");
        stcheck_hide_result = getIntent().getStringExtra("result");

        hide_CheckBox = (CheckBox) findViewById(R.id.hide_CheckBox); // 숨김버튼

        Button btnFinish = (Button) findViewById(R.id.btnFinish);
        // ChatActivity에서 완료 버튼을 누르면 MainAcitivity로 돌아가는 기능
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        hide_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 1000) { // 1초 동안 중복 클릭 무시
                }else{
                    // 체크박스 클릭 시 PasswdActivity를 시작
                    Intent myIntent = new Intent(ChatActivity.this, PasswdActivity.class);
                    startActivityForResult(myIntent, 1);
                }
                lastClickTime = currentTime;

            }
        });



        btnSend = (Button) findViewById(R.id.btnSend);
        etText = (EditText) findViewById(R.id.etText);


        //use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        String[] myDataset = {"test1", "test2", "test3", "test4"};
        mAdapter = new MyAdapter(chatArrayList, stEmail);
        recyclerView.setAdapter(mAdapter);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                // A new comment has been added, add it to the displayed list
                Chat chat = dataSnapshot.getValue(Chat.class);
                String commentKey = dataSnapshot.getKey();
                String stEmail = chat.getEmail(); // 이메일, 채팅, 숨김 여부 데이터 불러오기
                String stText = chat.getText();
                String stCheck_hide = chat.getCheck_hide();
                String stChat_time = chat.getChat_time();
                Log.d(TAG, "stText: "+ stText);
                Log.d(TAG, "stEmail: "+ stEmail);
                Log.d(TAG, "stCheck_hide: "+ stCheck_hide);
                Log.d(TAG, "stchat_time: "+ stChat_time);
                chatArrayList.add(chat);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Chat chat = dataSnapshot.getValue(Chat.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Chat chat = dataSnapshot.getValue(Chat.class);
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(ChatActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        DatabaseReference ref = database.getReference("message");
        ref.addChildEventListener(childEventListener);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hide_CheckBox.isChecked()){ //Check 박스 확인 후 비밀메시지 체크 여부
                    stcheck_hide = "yes";
                    hide_CheckBox.setChecked(false); //메시지 한 번 전송 후 자동 해제
                }else{
                    stcheck_hide = "no";
                }

                String stText = etText.getText().toString(); // 실질적으로 firebase에 올리는 채팅 메시지

                Toast.makeText(ChatActivity.this, "전송 완료", Toast.LENGTH_LONG).show();
                etText.getText().clear();//입력후 입력창 내용 삭제

                Calendar c = Calendar.getInstance(); // 날짜별로 데이터 구분
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                String datetime = dateformat.format(c.getTime());

                SimpleDateFormat dateformat_chat = new SimpleDateFormat("hh:mm", Locale.KOREA);
                String datetime_chat = dateformat_chat.format(c.getTime());


                DatabaseReference myRef = database.getReference("message").child(datetime);


                Hashtable<String, String> numbers = new Hashtable<String, String>(); // 실시간으로 DB에 전송된 글 올리기
                numbers.put("email", stEmail);
                numbers.put("text", stText);
                numbers.put("check_hide", stcheck_hide);
                numbers.put("chat_time", datetime_chat);


                myRef.setValue(numbers);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // PasswdActivity에서 성공적으로 작업을 완료하고 돌아온 경우(잠금 메시지 체크)
                stcheck_hide_result = data.getStringExtra("result");
                if (stcheck_hide_result != null && stcheck_hide_result.equals("success")) {
                    hide_CheckBox.setChecked(true);
                }else{
                    hide_CheckBox.setChecked(false);
                }
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                // PasswdActivity에서 성공적으로 작업을 완료하고 돌아온 경우(잠금 메시지 해제)
                chat_position = data.getIntExtra("position", -1);

                if (chat_position != -1) {

                    Chat chat = chatArrayList.get(chat_position);
                    chat.setCheck_hide("no");
                    mAdapter.notifyDataSetChanged();

                    Toast.makeText(ChatActivity.this, "잠금 해제에 성공했습니다.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ChatActivity.this, "잠금 해제에 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_chat_menu, menu);
        return true;
    }

    @Override // 2차 비밀번호 설정 탭화면
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_password:
                Intent intent = new Intent(this, SecondPwdActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
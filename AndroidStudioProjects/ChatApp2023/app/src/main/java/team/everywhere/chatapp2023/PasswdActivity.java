package team.everywhere.chatapp2023;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasswdActivity extends Activity {

    EditText etHide_Passwd;
    String passwd;
    int chat_position;
    FirebaseDatabase Firedatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_passwd);

        etHide_Passwd = (EditText) findViewById(R.id.hide_passwd);

    }

    //확인 버튼 클릭


    public void Hide_Passwd_Click(View v){
        DatabaseReference usersRef = Firedatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

        String stHide_Passwd = etHide_Passwd.getText().toString();
        chat_position = getIntent().getIntExtra("position", -1);
        if (passwd != null && passwd.equals(stHide_Passwd)) {
            //데이터 전달하기
            Intent intent = new Intent();
            intent.putExtra("result", "success");
            if(chat_position != -1)
                intent.putExtra("position", chat_position);

            setResult(RESULT_OK, intent);



            //액티비티(팝업) 닫기
            finish();
        } else {
            if(stHide_Passwd.isEmpty()) {
                Toast.makeText(PasswdActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
                // 비밀번호 미입력시
            } else{
                Toast.makeText(PasswdActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                etHide_Passwd.getText().clear();//입력후 입력창 내용 삭제
                // 비밀번호 오류시
            }

        }
    }

    public void Hide_Passwd_Back(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "false");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
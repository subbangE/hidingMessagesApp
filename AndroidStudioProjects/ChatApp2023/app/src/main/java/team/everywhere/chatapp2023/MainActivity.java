package team.everywhere.chatapp2023;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    EditText etId, etPassword; // 버튼 기능 구현을 위해 전역 변수 사용
    private FirebaseAuth mAuth;
    ProgressBar progressBar; // 전역 변수

    private long lastClickTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 입력된 ID와 Password를 받아옴
        etId = (EditText) findViewById(R.id.etId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        // activity_main에서 btnLogin 찾음
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail = etId.getText().toString();
                String stPassword = etPassword.getText().toString();
                if(stEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "이메일을 입력하세요", Toast.LENGTH_LONG).show();
                    return; // 아이디 미입력시
                }
                if(stPassword.isEmpty()){
                    Toast.makeText(MainActivity.this, "패스워드를 입력하세요", Toast.LENGTH_LONG).show();
                    return; // 패스워드 미입력시
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(stEmail, stPassword)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    long currentTime = System.currentTimeMillis();
                                    if (currentTime - lastClickTime < 1000) { // 1초 동안 중복 클릭 무시
                                    }else {
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String stUserEmail = user.getEmail();
                                        String stUserName = user.getDisplayName();
                                        Log.d(TAG, "stUserEmail: " + stUserEmail + ", stUserName: " + stUserName);
                                        Intent in = new Intent(MainActivity.this, ChatActivity.class);
                                        in.putExtra("email", stEmail.toString());
                                        startActivity(in); //메인 화면으로 전환
//                                    updateUI(user);
                                    }
                                    lastClickTime = currentTime;
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "비밀번호 오류", Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }

                            }
                        });
                // 버튼 클릭 후 "Login" 메시지 띄우는 기능
//                Toast.makeText( MainActivity.this, "Login", Toast.LENGTH_LONG).show();

            }
        });

        // 입력된 ID와 Password를 받아올 수 있게 하는 버튼 기능
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail = etId.getText().toString();
                String stPassword = etPassword.getText().toString();
                if(stEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "이메일을 입력하세요", Toast.LENGTH_LONG).show();
                    return; // 아이디 미입력시
                }
                if(stPassword.isEmpty()){
                    Toast.makeText(MainActivity.this, "패스워드를 입력하세요", Toast.LENGTH_LONG).show();
                    return; // 패스워드 미입력시
                }
                progressBar.setVisibility(View.VISIBLE);
                //잘받아 왔는지 확인 위함
//                Toast.makeText(MainActivity.this, "Email : "+stEmail+", password :"+stPassword,Toast.LENGTH_LONG).show();

                mAuth.createUserWithEmailAndPassword(stEmail, stPassword) // 회원 가입 기능
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    //Sign in success, update UI with the signed-in user's
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    updateUI(user);
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
}
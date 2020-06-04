package com.example.maskmap;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Menu1Activity extends AppCompatActivity {

    private EditText et_id;
    private Button btn_id;
    private TextView tv_id;
    private String str;
    private TextView tv_id2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);



        //액션바 타이틀 변경
        getSupportActionBar().setTitle(R.string.menu_1);
        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFF6666));


        et_id = findViewById(R.id.et_id);
        btn_id = findViewById(R.id.btn_id);
        tv_id = findViewById(R.id.tv_id);
        tv_id2 = findViewById(R.id.tv_id2);

        tv_id2.setText( "<준비물>\n성인: 주민등록증/운전면허증/여권\n 미성년: 청소년증/여권/학생증+주민등록등본\n 외국인: 외국인등록증/영주증/거소신고증\n 장애인: 장애인등록증(장애인복지카드)");

        btn_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = et_id.getText().toString().trim();
                setTextViewByYear(str);
            }
        });

    }

    protected void setTextViewByYear(String str) {

        // 만약 사용자가 입력한 내용이 숫자가 아니면 내용을 지우고 경고 메세지 출력
        try {
            // String to Int
            int year = Integer.parseInt(str);

            // 입력한 숫자 범위가 1900 이상 2020 이하인지 검사
            if(year < 1900 || year > 2020) {
                et_id.setText(null);
                Toast.makeText(this, "1900 이상 2020 이하의 숫자를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // year 마지막 숫자 계산
            int lastDigit = year%10;

            // 오늘 요일 계산 (1: 일요일, 2: 월요일, 3: 화요일, 4: 수요일, 5: 목요일, 6: 금요일, 7: 토요일)
            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            // 주말에는 모두 구매 가능
            if (today == 1 || today == 7) {
             tv_id.setText(year+"년생이신 분은 구매하실 수 있는 날입니다.");
             return;
            }

            switch(lastDigit) {

                // 월요일
                case 1 :
                case 6 :
                    if(today == 2) {
                        tv_id.setText(year+"년생이신 분은 구매하실 수 있는 날입니다.");
                    }
                    else {
                        tv_id.setText(year+"년생이신 분은 월요일에 구매하실 수 있습니다.");
                    }
                break;

                // 화요일
                case 2 :
                case 7 :
                    if(today == 3) {
                        tv_id.setText(year+"년생이신 분은 구매하실 수 있는 날입니다.");
                    }
                    else { tv_id.setText(year+"년생이신 분은 화요일에 구매하실 수 있습니다."); }
                break;

                // 수요일
                case 3 :
                case 8 :
                    if(today == 4) {
                        tv_id.setText(year+"년생이신 분은 구매하실 수 있는 날입니다.");
                    }
                    else { tv_id.setText(year+"년생이신 분은 수요일에 구매하실 수 있습니다."); }
                break;

                // 목요일
                case 4 :
                case 9 :
                    if(today == 5) {
                        tv_id.setText(year+"년생이신 분은 구매하실 수 있는 날입니다.");
                    }
                    else { tv_id.setText(year+"년생이신 분은 목요일에 구매하실 수 있습니다."); }
                break;

                // 금요일
                case 5 :
                case 0 :
                    if(today == 6) {
                        tv_id.setText(year+"년생이신 분은 구매하실 수 있는 날입니다.");
                    }
                    else { tv_id.setText(year+"년생이신 분은 금요일에 매하실 수 있습니다."); }
                break;
            }

        } catch(NumberFormatException e){
            et_id.setText(null);
            Toast.makeText(this, "숫자만 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

    }

}

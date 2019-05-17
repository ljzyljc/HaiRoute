package com.hai.jackie.hairoute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hai.jackie.router.EasyRouter;
import com.hai.jackie.router_annotations.Router;

@Router("main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_jump_to_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().with(MainActivity.this).navigate("second");
            }
        });
        findViewById(R.id.tv_jump_to_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().with(MainActivity.this).putString("name","haihai").navigate("third");
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToOtherModule();
            }
        });
    }
    public void skipToOtherModule(){
        EasyRouter.getInstance().with(this).navigate("otherMain");
    }







}

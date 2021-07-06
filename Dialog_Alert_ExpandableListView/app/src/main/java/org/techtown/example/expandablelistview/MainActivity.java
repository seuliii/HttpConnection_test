package org.techtown.example.expandablelistview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements ExpandableListView.OnChildClickListener{

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
//    TextView textView;
    TextView beginner;
    static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandable_list_view1);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        //리스트뷰 기본 아이콘 표시 여부
        expListView.setGroupIndicator(null);
        expListView.setOnChildClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("코스 선택");
        listDataHeader.add("효능별 자세");
        listDataHeader.add("내 통계");

        // Adding child data
        List<String> courseSelection = new ArrayList<String>();
        courseSelection.add("초급");
        courseSelection.add("중급");
        courseSelection.add("고급");
        courseSelection.add("코스 만들기");

        List<String> singlePose = new ArrayList<String>();
        singlePose.add("허리 통증");
        singlePose.add("어깨 통증");
        singlePose.add("골반 교정");


        List<String> myData = new ArrayList<String>();
        myData.add("한달");
        myData.add("6개월");
        myData.add("1년");



        listDataChild.put(listDataHeader.get(0), courseSelection); // Header, Child data
        listDataChild.put(listDataHeader.get(1), singlePose);
        listDataChild.put(listDataHeader.get(2), myData);
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
        final String selected = (String) listAdapter.getChild(groupPosition, childPosition);

//        Intent intent;

        switch(selected) {
            case "초급":

                //대화상자 dialog fragment
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("안내");
                builder.setMessage("초급 단계를 실행하시겠습니까?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String message = "예 버튼이 눌렸습니다. ";
//                        textView.setText(message);
                        Toast.makeText(MainActivity.this,
                                "예 버튼이 눌렀습니다",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, Beginner.class);
                        startActivity(intent);
                    }
                });
                builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String message = "취소 버튼이 눌렸습니다. ";
//                        textView.setText(message);
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String message = "아니오 버튼이 눌렸습니다. ";
//                        textView.setText(message);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                        /*
                        TextView  = view.findViewById(R.id.l);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                builder.setTitle("안내");
                                builder.setMessage("초급 단계를 실행하시겠습니까?");
                                builder.setIcon(android.R.drawable.ic_dialog_alert);

                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String message = "예 버튼이 눌렸습니다. ";
                                        textView.setText(message);
                                    }
                                });
                                builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String message = "취소 버튼이 눌렸습니다. ";
                                        textView.setText(message);
                                    }
                                });
                                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String message = "아니오 버튼이 눌렸습니다. ";
                                        textView.setText(message);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });

                        intent = new Intent(MainActivity.this, Beginner.class);
                        startActivity(intent);
                        */
                        break;


            case "중급": {
                Intent intent = new Intent(MainActivity.this, Intermediate.class);
                startActivity(intent);
                }
                break;

            case "고급": {
                Intent intent = new Intent(MainActivity.this, Advanced.class);
                startActivity(intent);
                }
                break;

            case "코스 만들기": {
                Intent intent = new Intent(MainActivity.this, CustomMode.class);
                startActivity(intent);
                }
                break;

            case "허리 통증": {
                Intent intent = new Intent(MainActivity.this, BackPainReliever.class);
                startActivity(intent);
            }
                break;

            case "어깨 통증": {
                Intent intent = new Intent(MainActivity.this, ShoulderPainReliever.class);
                startActivity(intent);
            }
                break;
            /*
            case "골반 교정":
                intent = new Intent(MainActivity.this, PelvisPainReliever.class);
                startActivity(intent);
                break;

            case "한달":
                intent = new Intent(MainActivity.this, OneMonth.class);
                startActivity(intent);
                break;

            case "6개월":
                intent = new Intent(MainActivity.this, HalfAnYear.class);
                startActivity(intent);
                break;

            case "1년":
                intent = new Intent(MainActivity.this, OneYear.class);
                startActivity(intent);
                break;

             */
        }
        return true;
    }
}

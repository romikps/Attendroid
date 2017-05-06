package com.example.romanpr.attendroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TeacherCourseActivity extends AppCompatActivity {
    ListView lv;
    String[] title = {"Jennifer Lawrance \n 130144040","Benedict Cumberbatch \n" +
            " 130144040","Martin Freeman \n" +
            " 130144040" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course);
        this.setTitle("Teacher Course");

        //ListView studentList=(ListView) findViewById(R.id.studentList);
        lv = (ListView) findViewById(R.id.listView1);
        CustomAdapter adapter = new CustomAdapter(this,title);
        lv.setAdapter(adapter);

        //ArrayAdapter dataAdaptor=ArrayAdapter.createFromResource(this, R.array.student_array,android.R.layout.simple_list_item_1);

        //ArrayAdapter<String> dataAdaptor=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, students);
        //studentList.setAdapter(dataAdaptor);


        //DATABASE
        //Once switched on take attendance, take latitude longitude values from the teacher,
        //and activate the students give attendance button by making location comparison
        //using database information about teacher's location.
        //and when you switch on, student's give attendance button will be enabled
        //if they are near to the teacher. Otherwise disable the student's give attendance button

    }
    class CustomAdapter extends ArrayAdapter<String> {
        Context context;
        String[] title;


        CustomAdapter(Context c, String[] title) {

            super(c, R.layout.listitem, title);
            this.context = c;
            this.title = title;

        }

        //In here, students will be showned in sorted order according to their points or attendance rates
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = vi.inflate(R.layout.listitem, parent, false);
            TextView titlee = (TextView) row.findViewById(R.id.textView1);
            int pos = position + 1;
            titlee.setText(+pos + ". " + title[position]);
            pos++;
            return row;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_main, menu);
        return true;
    }


}

package ruchajoshi.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls =new ArrayList<String>();
    ArrayList<String> celebNames =new ArrayList<String>();

    int chosenCeleb=0;

    ImageView imageview;

    int locationCorrectAnswer=0;
    String[] answers= new String[4];

    Button button0,button1,button2,button3;


    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url= new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();
                InputStream inputStream= urlConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);

                return bitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {

            String result="";

            URL url;
            HttpURLConnection urlConnection =null;

            try
            {
                url = new URL (strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);

                int data =reader.read();

                while(data!= -1){

                    char current = (char) data;

                    result+=current;
                    data= reader.read();

                }
               return result;

            }
            catch(Exception e){

                e.printStackTrace();;
            }


            return null;
        }
    }

    public void celebChosen(View view) {


        if(view.getTag().toString().equals(Integer.toString(locationCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
            }

            else{
            Toast.makeText(getApplicationContext(),"Wrong! It was" + celebNames.get(chosenCeleb),Toast.LENGTH_LONG).show();
        }

        celebNewQuestion();

    }


    public void celebNewQuestion() {

        Random random= new Random();
        chosenCeleb= random.nextInt(celebUrls.size());


        ImageDownloader imageDownloader= new ImageDownloader();
        Bitmap celebImage;

        try {
            celebImage=imageDownloader.execute(celebUrls.get(chosenCeleb)).get();
            imageview.setImageBitmap(celebImage);

            locationCorrectAnswer=random.nextInt(4);

            int incorrectanswerLocataion;

            for(int i=0;i<4;i++){

                if(i==locationCorrectAnswer){

                    answers[i]= celebNames.get(chosenCeleb);

                }
                else{
                    incorrectanswerLocataion= random.nextInt(celebUrls.size());
                    while (incorrectanswerLocataion==chosenCeleb){

                        incorrectanswerLocataion= random.nextInt(celebUrls.size());

                    }
                    answers[i]= celebNames.get(incorrectanswerLocataion);

                }

            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);

            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview= findViewById(R.id.imageview);

        button0=findViewById(R.id.button1);
        button1=findViewById(R.id.button2);
        button2=findViewById(R.id.button3);
        button3=findViewById(R.id.button4);

        DownloadTask downloadTask = new DownloadTask();

        String result =null;

        try {

            result = downloadTask.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult= result.split("<div class=\"sidebarContainer\">");

            Pattern pattern= Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher=pattern.matcher(splitResult[0]);

            while(matcher.find()){


                celebUrls.add(matcher.group(1));
                System.out.println(matcher.group(1));
            }

            pattern=Pattern.compile("alt=\"(.*?)\"");
            matcher=pattern.matcher(splitResult[0]);

            while(matcher.find()){

                celebNames.add(matcher.group(1));
                System.out.println(matcher.group(1));
            }

            celebNewQuestion();
            Log.i("Content URL",result);


        }

        catch (InterruptedException e){

            e.printStackTrace();

        }

        catch (ExecutionException e){

            e.printStackTrace();

        }
    }


}

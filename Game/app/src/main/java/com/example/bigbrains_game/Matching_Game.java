package com.example.bigbrains_game;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Matching_Game extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.matching_game_page);
        setUp();
    }

    //--------------------------Variable Declaration------------------------------------------------
        private List <Card> CardLayout = new ArrayList<Card>();
        private List<int []> PlacesUnavailable = new ArrayList<int [] >();
        private Button FirstMove =null;
        private Card hand [] = new Card[2] ;
        private int GameLevel=1,Score=0,Lives=3, timer=0;;
        private CountDownTimer Time;
    //----------------------------------------------------------------------------------------------

    //-----------------------Start Setters Methods--------------------------------------------------
    public void updateLevel(){
        TextView t= (TextView) findViewById(R.id.level_txt2);
        t.setText("Level : "+GameLevel);
    }

    public void updateLives(){
        TextView t= (TextView) findViewById(R.id.lives_txt);
        t.setText("Lives : "+Lives);
    }

    public void setUpTimer(){
        if(Time!=null){
            Time.cancel();
        }
        timer=(120-(GameLevel-1)*15)*1000;
        if(timer>0) {
            Time = new CountDownTimer(timer, 1000) {
                public void onFinish() {
                    ((TextView) findViewById(R.id.time_txt)).setText("TimeOver");
                    GameOver();
                }

                public void onTick(long milliSUntilFinish) {
                    ((TextView) findViewById(R.id.time_txt)).setText(String.valueOf(timer/1000));
                    timer-=1000;
                }
            }.start();
        }
    }

    public void updateScore(){
        TextView t= (TextView) findViewById(R.id.Score_txt);
        t.setText(String.valueOf(Score));
    }
    //----------------------------------------------------------------------------------------------

    //------------------------------Getter Methods--------------------------------------------------
    public int GetLevel(){
        TextView t= (TextView) findViewById(R.id.level_txt2);
        return Integer.parseInt(((String) t.getText()).replace("Level : ",""));
    }

    public int GetLives(){
        TextView t= (TextView) findViewById(R.id.lives_txt);
        return Integer.parseInt(((String) t.getText()).replace("Lives : ",""));
    }
    public int GetScore(){
        TextView t= (TextView) findViewById(R.id.Score_txt);
        return Integer.parseInt((String) t.getText());
    }
    //----------------------------------------------------------------------------------------------

    //-----------------------------Basic Methods----------------------------------------------------

    public int getID(String type,String name){
        return  getResources().getIdentifier(name,type, getPackageName());
    }

    public Card getCard(Button btn){
        for (Card c :
                CardLayout) {
            if(c.getBtnID()==btn) return c;
        }
        return  null;
    }

    //----------------------------------------------------------------------------------------------

    //--------------------------Game Logic----------------------------------------------------------
    public int [] RandomPlace(){
        int position1  =(int)(Math.random()*24+1),
            position2 =(int)(Math.random()*24+1);
        if(position1!=position2){
            return new int []{position1,position2};
        }else return RandomPlace();
    }

    public void LevelUP(View v){
        if(GameLevel<7){
            GameLevel = GetLevel() + 1;
            updateLevel();
            newGame(v);
        }else{
            Toast.makeText(getApplicationContext(),"Maximal Level",Toast.LENGTH_LONG).show();
        }
    }

    public void LevelDOWN(View v){
        if(GameLevel>0){
            GameLevel = GetLevel() - 1;
            updateLevel();
            newGame(v);
        }else{
            Toast.makeText(getApplicationContext(),"Minimal Level",Toast.LENGTH_LONG).show();
        }
    }

    public void ShuffleCards(){
        PlacesUnavailable.clear();
        for(int i=0;i<24;i+=2){
            boolean exist=false ;
            int r []=RandomPlace();
            do{
                exist = false;
                for (int[] UnAvailable :
                        PlacesUnavailable) {
                    if (r[0] == UnAvailable[0] || r[1] == UnAvailable[0]
                            || r[0] == UnAvailable[1] || r[1] == UnAvailable[1]) {
                        exist = true;
                        r=RandomPlace();
                        break;
                    }
                }
            }while(exist);

            CardLayout.get(i).setId(r[0]);
            CardLayout.get(i+1).setId(r[1]);
            setCardButton(CardLayout.get(i));
            setCardButton(CardLayout.get(i+1));
            PlacesUnavailable.add(r);
        }
        DisplayCards();
    }

    public void newGame(View v){
        setUp();
        ShuffleCards();
    }

    public void setCardButton(Card c){
        int id=c.getId();

        int btnID = getID("id","button"+id);

        c.setBtnID(findViewById(btnID));
    }

    public void setUp(){
        CardLayout.clear();
        Score =0;
        Lives =3;
        setUpTimer();
        updateLevel();
        updateScore();
        updateLives();
        for (int i=1;i<25;i++){
            int btnID = getID("id","button"+i),
                    imageID=(int)( ((i-1)/2)+1 ),
                CardImg=getID("drawable","matching_game_card_"+(imageID) );
            Card btn = new Card(i,(Button) findViewById(btnID),CardImg,imageID),
            btn2=new Card(i,(Button) findViewById(btnID),CardImg,imageID);
            CardLayout.add(btn);
        }
        int i=0;
        while(i<24){
            CardLayout.get(i).setTwin(CardLayout.get(i+1));
            i=i+2;
        }
        btnsActivation(true);

    }



    public synchronized void FlashBtn(Card c){
        Button btn = c.getBtnID();
        int colorID =c.getImage();
        btn.setBackgroundResource(colorID);
        btn.setText(""+c.getImageID());
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        btn.setBackgroundResource(R.drawable.matching_game_card_0);
                    }
                },
                3000);

    }

    public void DisplayCards(){
        for (Card c :
                CardLayout) {
            FlashBtn(c);
        }
    }

    public void DisplayTwin(View v){
        Button btn = findViewById(v.getId());
        Card selected= CardLayout.get(0);
        for (Card c :
                CardLayout) {
            if(c.getBtnID()==btn) {
                selected = c;
                break;
            }
        }
        FlashBtn(selected);
        FlashBtn(selected.getTwin());
    }

    public void CalculateScore(){
        Score=GetScore()+Lives*GameLevel*GameLevel;
        updateScore();
    }

    public void GamePlay(View v){
        if(FirstMove==null){

            FirstMove =(Button) findViewById(v.getId());
            Card card1 =getCard(FirstMove);
            FirstMove.setBackgroundResource(card1.getImage());

        }else{

            Button SecondMove =(Button) findViewById(v.getId());

            if(FirstMove!=SecondMove){

                Card card1=CardLayout.get(0),
                        card2=card1;

                for (Card c :
                        CardLayout) {

                    if(c.getBtnID()==FirstMove) card1=c;
                    else if(c.getBtnID()==SecondMove) card2=c;

                }
                if(!card1.isActive() && !card2.isActive()){
                    if (card1.checkTwin(card2)){

                        card1.getBtnID().setBackgroundResource(card1.getImage());
                        card1.setActive(true);

                        card2.setActive(true);
                        card2.getBtnID().setBackgroundResource(card2.getImage());

                        CalculateScore();

                    }else{
                        if(Lives>=1){

                            Lives=GetLives()-1;
                            FlashBtn(card1);
                            FlashBtn(card2);
                            updateLives();

                        }else{
                            GameOver();
                        }
                    }
                }
            }
            FirstMove=null;
            if(Completed()){
                Toast.makeText(getApplicationContext(),"Game Completed Congrats",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void  GameOver(){
        Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();
        btnsActivation(false);
    }
    public void btnsActivation(boolean status){
        for (Card c :
                CardLayout) {
            c.getBtnID().setEnabled(status);
        }
    }
    public boolean Completed(){
        for (Card c :
                CardLayout) {
            if (!c.isActive()) return false;
        }
        return true;
    }
    //----------------------------------------------------------------------------------------------
}

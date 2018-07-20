package com.greece.nasiakouts.babysitterfinder.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.greece.nasiakouts.babysitterfinder.Adapters.SittersResultRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SittersResultActivity extends AppCompatActivity {

    @BindView(R.id.available_sitters_rv)
    RecyclerView mAvailableSittersRv;

    private SittersResultRvAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitters_result);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.sitters_results);

        User user1 = new User("ads@dfdf.com", "lll");
        user1.setFullName("Maria Nakkou");
        user1.setPhoneNumber("69804455");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 10);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.YEAR, 95);
        user1.setDateBornTimestamp(cal.getTime().getTime());
/*
        Babysitter sitter1 = new Babysitter(user1, "Agiou Tryfwnos 25", 10.5, "EUR", null);
        sitter1.setMaxKids(4);
        sitter1.setMinAge(1);
        sitter1.setIntroduction("I love kids. I love pets, I love you i am a lovign persong lalalaal lalall alal lalal lal al al lal alal a lal ala la l");

        User user2 = new User("22ds@dfdf.com", "222");
        user2.setFullName("Giannis Lolos");
        user2.setPhoneNumber("69793453");
        user2.setDateBornTimestamp(cal.getTime().getTime());

        Babysitter sitter2 = new Babysitter(user2, "Vouliagmenis 33", 11.5, "EUR", null);
        sitter2.setMaxKids(6);
        sitter2.setMinAge(2);
        sitter2.setIntroduction("I love kids. I love pets, I love you i am a lovign persong lalalaal lalall alal lalal lal al al lal alal a lal ala la l");

        ArrayList<Babysitter> tempTesting = new ArrayList<>();
        tempTesting.add(0, sitter1);
        tempTesting.add(1, sitter2);
*/
        mAdapter = new SittersResultRvAdapter(this, null);
        mAvailableSittersRv.setAdapter(mAdapter);
        mAvailableSittersRv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void insertAppointmentToDb(String userId, String sitterEmail) {
        // todo
    }
}

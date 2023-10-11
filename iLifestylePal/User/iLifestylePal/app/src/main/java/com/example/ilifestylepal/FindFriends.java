package com.example.ilifestylepal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FindFriends extends AppCompatActivity {

    private LinearLayout linear_find, linear_requests, linear_friends;
    private int btn_active = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linear_find = findViewById(R.id.linear_find);
        linear_requests = findViewById(R.id.linear_requests);
        linear_friends = findViewById(R.id.linear_friends);

        SetDefaultFragment();
        GoToFindFriends();
        GoToFriendRequests();
        GoToFriendsList();
    }

    private void SetDefaultFragment() {
        FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, findFriendsFragment);
        fragmentTransaction.commit();
    }

    private void GoToFindFriends() {
        linear_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_active != 1) {
                    btn_active = 1;

                    Toolbar mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
                    mToolbar.setTitle("Find Friends");

                    FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, findFriendsFragment);
                    fragmentTransaction.commit();

                    // Change the background
                    linear_find.setBackground(getResources().getDrawable(R.drawable.border_style_5));
                    linear_requests.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                    linear_friends.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                }
            }
        });
    }

    private void GoToFriendRequests() {
        linear_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_active != 2) {
                    btn_active = 2;

                    Toolbar mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
                    mToolbar.setTitle("Friend Requests");

                    FindFriendsRequestsFragment friendsRequestsFragment = new FindFriendsRequestsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, friendsRequestsFragment);
                    fragmentTransaction.commit();

                    // Change the background
                    linear_find.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                    linear_requests.setBackground(getResources().getDrawable(R.drawable.border_style_5));
                    linear_friends.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                }
            }
        });
    }

    private void GoToFriendsList() {
        linear_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_active != 3) {
                    btn_active = 3;

                    Toolbar mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
                    mToolbar.setTitle("Friends List");

                    FindFriendsFriendsFragment friendsListFragment = new FindFriendsFriendsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, friendsListFragment);
                    fragmentTransaction.commit();

                    // Change the background
                    linear_find.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                    linear_requests.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                    linear_friends.setBackground(getResources().getDrawable(R.drawable.border_style_5));
                }
            }
        });
    }
}
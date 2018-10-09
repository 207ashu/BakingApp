package com.neurobreach.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.neurobreach.bakingapp.fragments.IngredientDetailFragment;
import com.neurobreach.bakingapp.model.IngredientsModel;

import java.util.List;

public class IngredientDetailActivity extends AppCompatActivity {

    private List<IngredientsModel> ingredientsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Intent.EXTRA_TEXT);
        ingredientsModelList = (List<IngredientsModel>) bundle.getSerializable("key");

        IngredientDetailFragment detailFragment = new IngredientDetailFragment();
        detailFragment.setIngredientsModelList(ingredientsModelList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.ingredient_detail_container, detailFragment)
                .commit();
    }

}

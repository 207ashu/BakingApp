package com.neurobreach.bakingapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.neurobreach.bakingapp.fragments.IngredientDetailFragment;
import com.neurobreach.bakingapp.fragments.RecipeIngredientFragment;
import com.neurobreach.bakingapp.fragments.RecipeStepFragment;
import com.neurobreach.bakingapp.fragments.StepDetailFragment;
import com.neurobreach.bakingapp.model.IngredientsModel;
import com.neurobreach.bakingapp.model.RecipeModel;
import com.neurobreach.bakingapp.model.StepsModel;
import com.neurobreach.bakingapp.widget.IngredientListService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeIngredientFragment.OnIngredientItemClickListener,
        RecipeStepFragment.OnStepItemClickListener {

    private RecipeModel recipeModel;
    private boolean mTwoPane;
    private boolean mIngredientSelected = true;
    private StepsModel stepsModelSave;
    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.step_title_textView) TextView stepTitleTextView;
    @BindView(R.id.parent_container) FrameLayout parentContainer;
    private Unbinder unbinder;
    public static String recipeTitle = "Recipe Title";
    public static List<IngredientsModel> ingredientsModelList = new ArrayList<IngredientsModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        recipeModel = (RecipeModel) bundle.getSerializable("key");
        mTwoPane = false;
        titleTextView.setText(recipeModel.getName());
        getSupportActionBar().setTitle(recipeModel.getName());

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/pacifico-regular.ttf");
        titleTextView.setTypeface(customFont);
        stepTitleTextView.setTypeface(customFont);

        FragmentManager fragmentManager = getSupportFragmentManager();

        RecipeIngredientFragment ingredientFragment = new RecipeIngredientFragment();
        ingredientFragment.setIngredientsModelList(recipeModel.getIngredients());

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_ingredient_container, ingredientFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_ingredient_container, ingredientFragment)
                    .commit();
        }

        RecipeStepFragment stepFragment = new RecipeStepFragment();
        stepFragment.setStepsModelList(recipeModel.getSteps());

        fragmentManager.beginTransaction()
                .add(R.id.recipe_step_container, stepFragment)
                .commit();

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
            IngredientDetailFragment ingredientDetailFragment = new IngredientDetailFragment();
            ingredientDetailFragment.setIngredientsModelList(recipeModel.getIngredients());
            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        .add(R.id.detail_container, ingredientDetailFragment)
                        .commit();
            } else {
                Bundle bundle1 = savedInstanceState.getBundle("bun");
                mIngredientSelected = bundle1.getBoolean("bol");
                if (mIngredientSelected) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.detail_container, ingredientDetailFragment)
                            .commit();
                } else {
                    StepsModel stepsModel = (StepsModel) bundle1.getSerializable("ser");
                    stepsModelSave = stepsModel;
                    StepDetailFragment stepDetailFragment = new StepDetailFragment();
                    stepDetailFragment.setStepsModel(stepsModel);
                    fragmentManager.beginTransaction()
                            .replace(R.id.detail_container, stepDetailFragment)
                            .commit();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ser", stepsModelSave);
        bundle.putBoolean("bol", mIngredientSelected);
        outState.putBundle("bun", bundle);
    }

    @Override
    public void onIngredientItemClicked(List<IngredientsModel> ingredientsModelList) {
        if (mTwoPane) {
            mIngredientSelected = true;
            IngredientDetailFragment ingredientDetailFragment = new IngredientDetailFragment();
            ingredientDetailFragment.setIngredientsModelList(ingredientsModelList);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, ingredientDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, IngredientDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("key", (Serializable) ingredientsModelList);
            intent.putExtra(Intent.EXTRA_TEXT, bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onStepItemClicked(StepsModel stepsModel) {
        if (mTwoPane) {
            stepsModelSave = stepsModel;
            mIngredientSelected = false;
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setStepsModel(stepsModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, stepDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ser", stepsModel);
            intent.putExtra(Intent.EXTRA_TEXT, bundle);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_recipe_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        boolean recipeAdded;
        if (itemId == R.id.action_add) {
            recipeTitle = recipeModel.getName();
            ingredientsModelList = recipeModel.getIngredients();
            recipeAdded = IngredientListService.startActionChangeIngredientList(this);

            if (recipeAdded)
                Snackbar.make(parentContainer, R.string.widget_added_text, Snackbar.LENGTH_SHORT).show();
            else
                Snackbar.make(parentContainer, R.string.widget_not_added_text, Snackbar.LENGTH_SHORT).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

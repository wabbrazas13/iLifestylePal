package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNode_Inputs {

    private Context context;
    private Button addIngredientButton;
    private RecyclerView rvIngredients;
    private EditText ingredientEditText, calorieEditText, foodNameEditText, urlEditText;
    private List<String> ingredientsList = new ArrayList<>();


    private String fname;
    private String kcal;
    private String cat;
    private String url;
    private ArrayList<String> list;

    public AddNode_Inputs(Context context) {
        this.context = context;
    }
    public void displayDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.app_delete_this, null);

        addIngredientButton = dialogView.findViewById(R.id.addIngredientButton);
        rvIngredients = dialogView.findViewById(R.id.rvIngredients);
        ingredientEditText = dialogView.findViewById(R.id.ingredientEditText);
        calorieEditText = dialogView.findViewById(R.id.calorieEditText);
        foodNameEditText = dialogView.findViewById(R.id.foodNameEditText);
        urlEditText = dialogView.findViewById(R.id.urlEditText);

        rvIngredients.setLayoutManager(new LinearLayoutManager(context));
        IngredientAdapter adapter = new IngredientAdapter(ingredientsList);
        rvIngredients.setAdapter(adapter);

        // In the onClickListener of the addIngredientButton
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = ingredientEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient)) {
                    ingredientsList.add(ingredient);
                    rvIngredients.getAdapter().notifyItemInserted(ingredientsList.size() - 1);
                    ingredientEditText.setText("");
                }
            }
        });

        // Set the custom layout to the dialog
        dialogBuilder.setView(dialogView);

        // Add the positive button
        dialogBuilder.setPositiveButton("Add Food", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        // Add the negative button
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform action for negative button
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void foodValue() {
        list.clear();
        fname = "Berry Chia Pudding";
        kcal = "343 ";
        cat = "Breakfast";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FBerry%20Chia%20Pudding.jpg?alt=media&token=e19dafc9-3056-4001-a067-58a93152fca5";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("1 ¾ cups blackberries, raspberries or diced mango (fresh or frozen), divided");
        ingredients.add("1 cup unsweetened almond milk or milk of choice");
        ingredients.add("¼ cup chia seeds");
        ingredients.add("1 tablespoon pure maple syrup");
        ingredients.add("¾ teaspoon vanilla extract");
        ingredients.add("½ cup whole-milk plain Greek yogurt");
        ingredients.add("¼ cup granola");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }
    private void a(){
        //list.clear();
        fname = "Mixed Greens with Lentils & Sliced Apple";
        kcal = "347 ";
        cat = "Lunch";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FMixed%20Greens%20with%20Lentils%20%26%20Sliced%20Apple.jpg?alt=media&token=df2fe0be-aa12-4bcb-88d0-428ee63fa34c";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("1 ½ cups mixed salad greens");
        ingredients.add("½ cup cooked lentils");
        ingredients.add("1 apple, cored and sliced, divided");
        ingredients.add("1 ½ tablespoons crumbled feta cheese");
        ingredients.add("1 tablespoon red-wine vinegar");
        ingredients.add("2 teaspoons extra-virgin olive oil");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }
    private void b(){
        //list.clear();
        fname = "Grilled Chicken with Red Pepper-Pecan Romesco Sauce";
        kcal = "77";
        cat = "Dinner";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FGrilled%20Chicken%20with%20Red%20Pepper-Pecan%20Romesco%20Sauce.jpg?alt=media&token=3c4442ce-bc43-49c7-85de-188b2bd55e93";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("2 medium red bell peppers");
        ingredients.add("1 medium tomato");
        ingredients.add("1 pound chicken cutlets");
        ingredients.add("¾ teaspoon salt, divided");
        ingredients.add("½ teaspoon ground pepper, divided");
        ingredients.add("½ cup chopped pecans, toasted");
        ingredients.add("1 clove garlic");
        ingredients.add("2 tablespoons extra-virgin olive oil");
        ingredients.add("1 tablespoon red-wine vinegar");
        ingredients.add("¼ teaspoon crushed red pepper");
        ingredients.add("Chopped scallions for garnish");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void c(){
      //  list.clear();
        fname = "Spiced Grilled Chicken with Cauliflower Rice Tabbouleh";
        kcal = "85";
        cat = "Dinner";
        url = "";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("5 tablespoons extra-virgin olive oil, divided");
        ingredients.add("2 ½ teaspoons ground cumin, divided");
        ingredients.add("1 ½ teaspoons dried marjoram");
        ingredients.add("¾ teaspoon salt, divided");
        ingredients.add("¼ teaspoon ground allspice");
        ingredients.add("¼ teaspoon cayenne pepper");
        ingredients.add("1 pound boneless, skinless chicken breast, trimmed");
        ingredients.add("¼ cup lemon juice");
        ingredients.add("2 cups fresh riced cauliflower (see Tip)");
        ingredients.add("2 cups flat-leaf parsley leaves");
        ingredients.add("1 cup diced cucumber");
        ingredients.add("1 cup halved cherry tomatoes");
        ingredients.add("¼ cup sliced scallions");


        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void d(){
      //  list.clear();
        fname = "Avocado-Egg Toast";
        kcal = "271";
        cat = "Breakfast";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FAvocado-Egg%20Toast.jpg?alt=media&token=2330532c-8fc3-4a1d-b82b-5ef7eb15db53";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("¼ avocado");
        ingredients.add("¼ teaspoon ground pepper");
        ingredients.add("⅛ teaspoon garlic powder");
        ingredients.add("1 slice whole-wheat bread, toasted");
        ingredients.add("1 large egg, fried");
        ingredients.add("1 teaspoon Sriracha (Optional)");
        ingredients.add("1 tablespoon scallion, sliced (Optional)");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void e(){
      //  list.clear();
        fname = "Avocado-Egg Toast";
        kcal = "271";
        cat = "Breakfast";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FAvocado-Egg%20Toast.jpg?alt=media&token=2330532c-8fc3-4a1d-b82b-5ef7eb15db53";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("¼ avocado");
        ingredients.add("¼ teaspoon ground pepper");
        ingredients.add("⅛ teaspoon garlic powder");
        ingredients.add("1 slice whole-wheat bread, toasted");
        ingredients.add("1 large egg, fried");
        ingredients.add("1 teaspoon Sriracha (Optional)");
        ingredients.add("1 tablespoon scallion, sliced (Optional)");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void f(){
     //   list.clear();
        fname = "Charred Shrimp, Pesto & Quinoa Bowls";
        kcal = "107";
        cat = "Dinner";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FCharred%20Shrimp%2C%20Pesto%20%26%20Quinoa%20Bowls.jpg?alt=media&token=25c78874-dd12-4ed8-9077-f1c4be046652";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("⅓ cup prepared pesto");
        ingredients.add("2 tablespoons balsamic vinegar");
        ingredients.add("1 tablespoon extra-virgin olive oil");
        ingredients.add("½ teaspoon salt");
        ingredients.add("¼ teaspoon ground pepper");
        ingredients.add("1 pound peeled and deveined large shrimp (16-20 count), patted dry");
        ingredients.add("4 cups arugula");
        ingredients.add("2 cups cooked quinoa");
        ingredients.add("1 cup halved cherry tomatoes");
        ingredients.add("1 avocado, diced");


        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void g(){
      //  list.clear();
        fname = "Tuna, White Bean & Dill Salad";
        kcal = "296";
        cat = "Lunch";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FTuna%2C%20White%20Bean%20%26%20Dill%20Salad.jpg?alt=media&token=7502bf60-1370-464b-87dd-1fd267f5b748";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("1 (15 ounce) can no-salt-added cannellini beans (white kidney beans), rinsed and drained");
        ingredients.add("2 (5 ounce) cans solid white tuna (water pack), drained and broken into chunks");
        ingredients.add("⅓ cup chopped red onion (1 small)");
        ingredients.add("3 tablespoons plus 1 teaspoon, honey Dijon-style mustard, divided");
        ingredients.add("2 tablespoons light mayonnaise");
        ingredients.add("2 tablespoons cider vinegar, divided");
        ingredients.add("1 ⅛ teaspoons dried dill, divided");
        ingredients.add("2 tablespoons olive oil");
        ingredients.add("⅛ teaspoon kosher salt");
        ingredients.add("6 cups fresh baby spinach");
        ingredients.add("2 cups cubed, cooked and chilled beets (see Tip)");
        ingredients.add("1 sprig Chopped fresh dill or ground pepper");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }
    private void h(){
      //  list.clear();
        fname = "Skillet Lemon Chicken & Potatoes with Kale";
        kcal = "94";
        cat = "Dinner";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FSkillet%20Lemon%20Chicken%20%26%20Potatoes%20with%20Kale.jpg?alt=media&token=4648d4d3-5546-4695-9079-219afbd80803";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("3 tablespoons extra-virgin olive oil, divided");
        ingredients.add("1 pound boneless, skinless chicken thighs, trimmed");
        ingredients.add("½ teaspoon salt, divided");
        ingredients.add("½ teaspoon ground pepper, divided");
        ingredients.add("1 pound baby Yukon Gold potatoes, halved lengthwise");
        ingredients.add("½ cup low-sodium chicken broth");
        ingredients.add("1 large lemon, sliced and seeds removed");
        ingredients.add("4 cloves garlic, minced");
        ingredients.add("1 tablespoon chopped fresh tarragon");
        ingredients.add("6 cups baby kale");


        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void j(){
      //  list.clear();
        fname = "Garlic Butter-Roasted Salmon with Potatoes & Asparagus";
        kcal = "131";
        cat = "Dinner";
        url = "https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2FGarlic%20Butter-Roasted%20Salmon%20with%20Potatoes%20%26%20Asparagus.jpg?alt=media&token=7f568081-e6e7-4cee-9f32-ee5fd4c6bf4a";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("1 pound baby Yukon Gold potatoes, halved");
        ingredients.add("2 tablespoons extra-virgin olive oil, divided");
        ingredients.add("¾ teaspoon salt, divided");
        ingredients.add("½ teaspoon ground pepper, divided");
        ingredients.add("12 ounces asparagus, trimmed");
        ingredients.add("2 tablespoons melted butter");
        ingredients.add("1 tablespoon lemon juice");
        ingredients.add("2 cloves garlic, minced");
        ingredients.add("1 ¼ pounds salmon fillet, skinned and cut into 4 portions");
        ingredients.add("Chopped parsley for garnish");

        insertFoodData(fname, kcal, cat, url, ingredients);
    }

    private void insertFoodData(String foodName, String calorie, String category, String url, List<String> ingredients) {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Create a new map to store the food data
        Map<String, Object> foodData = new HashMap<>();

        // Add the ingredients to the food data map
        Map<String, Boolean> ingredientsMap = new HashMap<>();
        for (String ingredient : ingredients) {
            ingredientsMap.put(ingredient, true);
        }
        foodData.put("Ingredients", ingredientsMap);

        // Add the calorie, food name, category, and URL to the food data map
        foodData.put("calorie", calorie);
        foodData.put("foodname", foodName);
        foodData.put("category", category);
        foodData.put("url", url);

        // Push the food data to the "Meal" node under the "Food" node under the "Maintenance" node
        databaseRef.child("Maintenance").child("Food").child("Meal").child(foodName).setValue(foodData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Food Successfully Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to add food", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void InsertThroughViews(){
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Create a new map to store the food data
        Map<String, Object> foodData = new HashMap<>();

        // Add the ingredients to the food data map
        Map<String, Boolean> ingredients = new HashMap<>();
        for (String ingredient : ingredientsList) {
            ingredients.put(ingredient, true);
        }
        foodData.put("Ingredients", ingredients);

        // Add the calorie, food name, and URL to the food data map
        foodData.put("calorie", calorieEditText.getText().toString());
        foodData.put("foodname", foodNameEditText.getText().toString());
        foodData.put("url", urlEditText.getText().toString());

        // Push the food data to the "Meal" node under the "Food" node under the "Maintenance" node
        databaseRef.child("Maintenance").child("Food").child("Meal")
                .child(foodNameEditText.getText().toString()).setValue(foodData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Food Successfully Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to add food", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
        private List<String> ingredientsList;

        public IngredientAdapter(List<String> ingredientsList) {
            this.ingredientsList = ingredientsList;
        }

        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_delete_this2, parent, false);
            return new IngredientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
            String ingredient = ingredientsList.get(position);
            holder.bind(ingredient);
        }

        @Override
        public int getItemCount() {
            return ingredientsList.size();
        }

        class IngredientViewHolder extends RecyclerView.ViewHolder {
            private TextView ingredientTextView;

            public IngredientViewHolder(@NonNull View itemView) {
                super(itemView);
                ingredientTextView = itemView.findViewById(R.id.tv_Ingredients);
            }

            public void bind(String ingredient) {
                ingredientTextView.setText(ingredient);
            }
        }
    }


}

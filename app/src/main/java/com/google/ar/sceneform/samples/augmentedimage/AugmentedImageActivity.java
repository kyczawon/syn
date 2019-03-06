/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.samples.common.helpers.SnackbarHelper;
import com.google.ar.sceneform.ux.ArFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 */
public class AugmentedImageActivity extends AppCompatActivity {

  private ArFragment arFragment;
  private ImageView fitToScanView;

  // Augmented image and its associated center pose anchor, keyed by the augmented image in
  // the database.
  private Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    fitToScanView = findViewById(R.id.image_view_fit_to_scan);

    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

    // set the spinner data programmatically, from a string array or list

    // (1) get a reference to the spinner
        Spinner spinner1 = findViewById(R.id.language_spinner);

    // (3) create an adapter from the list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getBaseContext(),
                android.R.layout.simple_spinner_item,
                Utils.getLanguages()
        );

    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // (4) set the adapter on the spinner
    spinner1.setAdapter(adapter);

    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        Utils.setTargetLanguage(Utils.languageCodes.get(position));
        translateStuff();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        // your code here
      }

    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (augmentedImageMap.isEmpty()) {
      fitToScanView.setVisibility(View.VISIBLE);
    }
  }

  /**
   * Registered with the Sceneform Scene object, this method is called at the start of each frame.
   *
   * @param frameTime - time since last frame.
   */
  public void onUpdateFrame(FrameTime frameTime) {
    Frame frame = arFragment.getArSceneView().getArFrame();

    // If there is no frame or ARCore is not tracking yet, just return.
    if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
      return;
    }

    Collection<AugmentedImage> updatedAugmentedImages =
        frame.getUpdatedTrackables(AugmentedImage.class);

//    if (updatedAugmentedImages.isEmpty()) {
//      fitToScanView.setVisibility(View.VISIBLE);
//    }

    for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      switch (augmentedImage.getTrackingState()) {
        case PAUSED:
          break;

        case TRACKING:
          // Have to switch to UI Thread to update View.
          fitToScanView.setVisibility(View.GONE);

          // Create a new anchor for newly found images.
          if (!augmentedImageMap.containsKey(augmentedImage)) {


            //remove previous nodes
            for (AugmentedImageNode imageNode: augmentedImageMap.values()) {
              arFragment.getArSceneView().getScene().removeChild(imageNode);
            }
            augmentedImageMap.clear();

            //add current nodes
            AugmentedImageNode node = new AugmentedImageNode(this, augmentedImage.getIndex());
            node.setImage(augmentedImage);
            augmentedImageMap.put(augmentedImage, node);
            arFragment.getArSceneView().getScene().addChild(node);
          }
          break;

        case STOPPED:
          augmentedImageMap.remove(augmentedImage);
          break;
      }
    }
  }



  private void translateStuff() {

    AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
      @Override
      protected String doInBackground(String... input) {
        String message = "";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://translation.googleapis.com/language/translate/v2" +
                        "?q=" + Utils.descriptions[0] +
                        "&q=" + Utils.descriptions[1] +
                        "&target=" + Utils.targetLanguage +
                        "&key=" + "AIzaSyBAEGDww4VJ1UmJI2Du296txX3aPEpUO_g")
                .build();
        try {
          Response response = client.newCall(request).execute();
          assert response.body() != null;
          JSONObject jmessage = new JSONObject(response.body().string());

          JSONArray jsonArray = jmessage.getJSONObject("data").getJSONArray("translations");

          String[] list = new String[4];

          for(int i = 0; i < Utils.descriptions.length; i++){
            list[i] = jsonArray.getJSONObject(i).getString("translatedText");
          }

//          System.out.println(Array.toString(list));

          Utils.setTranslations(list);

        } catch (IOException e) {
          e.printStackTrace();
          message = "Could not connect to server";
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return message;
      }

      @Override
      protected void onPostExecute(String message) {
        onUpdateFrame(null);
      }
    };

    asyncTask.execute();
  }
}

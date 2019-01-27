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

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})


public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

  // The augmented image represented by this node.
  private AugmentedImage image;

  // Models of the 4 corners.  We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
  private static CompletableFuture<ModelRenderable> ulCorner;
  private static CompletableFuture<ModelRenderable> urCorner;
  private static CompletableFuture<ModelRenderable> lrCorner;
  private static CompletableFuture<ModelRenderable> llCorner;
  private static CompletableFuture<ModelRenderable> hamburger;
  private static CompletableFuture<ModelRenderable> pizza;
  private static CompletableFuture<ModelRenderable> rice;
  private static CompletableFuture<ModelRenderable> sushi;
  private static CompletableFuture<ModelRenderable> button;
  private static CompletableFuture<ViewRenderable> details;
    private static CompletableFuture<ViewRenderable> allergens;

  private Node currentFood;


  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
    if (hamburger == null) {
        hamburger =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/hamburger.sfb"))
              .build();
      pizza =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("models/pizza.sfb"))
                      .build();
      rice =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("models/rice.sfb"))
                      .build();
      sushi =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("models/sushi.sfb"))
                      .build();
      button =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("models/button.sfb"))
                      .build();

      details =
              ViewRenderable.builder()
                      .setView(context, R.layout.description)
                      .build();

      allergens =
                ViewRenderable.builder()
                        .setView(context, R.layout.allergens)
                        .build();
}
  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The corners are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corners.
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!hamburger.isDone()) {
      CompletableFuture.allOf(hamburger)
          .thenAccept((Void aVoid) -> setImage(image))
          .exceptionally(
              throwable -> {
                Log.e(TAG, "Exception loading", throwable);
                return null;
              });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    // Make the 4 corner nodes.
    Vector3 localPosition = new Vector3();

    localPosition.set(0.0f,0.0f,-0.07f);
    Node buttonNode = new Node();
    buttonNode.setParent(this);
    buttonNode.setLocalPosition(localPosition);
    buttonNode.setRenderable(button.getNow(null));
    buttonNode.setLocalScale(new Vector3(0.03f,0.07f,0.035f));

    localPosition.set(0.0f,0.0f,-0.03f);
    Node buttonNode2 = new Node();
    buttonNode2.setParent(this);
    buttonNode2.setLocalPosition(localPosition);
    buttonNode2.setRenderable(button.getNow(null));
    buttonNode2.setLocalScale(new Vector3(0.03f,0.07f,0.035f));

    localPosition.set(0.0f,0.0f,0.01f);
    Node buttonNode3 = new Node();
    buttonNode3.setParent(this);
    buttonNode3.setLocalPosition(localPosition);
    buttonNode3.setRenderable(button.getNow(null));
    buttonNode3.setLocalScale(new Vector3(0.03f,0.07f,0.035f));

    localPosition.set(0.0f,0.0f,0.05f);
    Node buttonNode4 = new Node();
    buttonNode4.setParent(this);
    buttonNode4.setLocalPosition(localPosition);
    buttonNode4.setRenderable(button.getNow(null));
    buttonNode4.setLocalScale(new Vector3(0.03f,0.07f,0.035f));

    localPosition.set(0.0f, 0.0f, -0.6f * image.getExtentZ());
    Node detailsNode = new Node();
    detailsNode.setParent(this);
    detailsNode.setLocalPosition(localPosition);
    detailsNode.setLocalScale(new Vector3(0.25f,0.25f,0.25f));
    detailsNode.setLocalRotation(new Quaternion( 0.383f,0,0, -0.924f));

      localPosition.set(0.9f * image.getExtentX(), 0.0f, 0.0f);
      Node allergensNode = new Node();
      allergensNode.setParent(this);
      allergensNode.setLocalPosition(localPosition);
      allergensNode.setLocalScale(new Vector3(0.25f,0.25f,0.25f));
      allergensNode.setLocalRotation(new Quaternion(  0.383f,0,0, -0.924f));

    //current food
    localPosition.set(-1f * image.getExtentX(), 0.0f, 0);
    currentFood = new Node();
    currentFood.setParent(this);
    currentFood.setLocalPosition(localPosition);


    buttonNode.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,1,0,1));
        currentFood.setRenderable(sushi.getNow(null));
        currentFood.setLocalScale(new Vector3(0.25f,0.25f,0.25f));

        detailsNode.setRenderable(details.getNow(null));

        TextView text = details.getNow(null).getView().findViewById(R.id.header);
        text.setText(Utils.translations[0]);

        TextView calories = details.getNow(null).getView().findViewById(R.id.calories);
        calories.setText("256 Calories");

        allergensNode.setRenderable(allergens.getNow(null));

        TextView allergensTextView = allergens.getNow(null).getView().findViewById(R.id.allergens_header);
        allergensTextView.setText(Utils.allergens[0]);
    });

    buttonNode2.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0, 0, 0, 0));
        currentFood.setRenderable(hamburger.getNow(null));
        currentFood.setLocalScale(new Vector3(0.070f, 0.070f, 0.070f));

        detailsNode.setRenderable(details.getNow(null));

        TextView text = details.getNow(null).getView().findViewById(R.id.header);
        text.setText(Utils.translations[1]);

        TextView calories = details.getNow(null).getView().findViewById(R.id.calories);
        calories.setText("127 Calories");

        allergensNode.setRenderable(allergens.getNow(null));

        TextView allergensTextView = allergens.getNow(null).getView().findViewById(R.id.allergens_header);
        allergensTextView.setText(Utils.allergens[1]);
    });

    buttonNode3.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,0,0,0));
        currentFood.setRenderable(pizza.getNow(null));
        currentFood.setLocalScale(new Vector3(0.032f,0.032f,0.032f));

        TextView text = details.getNow(null).getView().findViewById(R.id.header);
        text.setText(Utils.translations[2]);

        detailsNode.setRenderable(details.getNow(null));

        TextView calories = details.getNow(null).getView().findViewById(R.id.calories);
        calories.setText("347 Calories");

        allergensNode.setRenderable(allergens.getNow(null));

        TextView allergensTextView = allergens.getNow(null).getView().findViewById(R.id.allergens_header);
        allergensTextView.setText(Utils.allergens[2]);
    });

    buttonNode4.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,0,0,0));
        currentFood.setRenderable(rice.getNow(null));
        currentFood.setLocalScale(new Vector3(0.14f,0.14f,0.14f));

        detailsNode.setRenderable(details.getNow(null));

        TextView text = details.getNow(null).getView().findViewById(R.id.header);
        text.setText(Utils.translations[3]);

        TextView calories = details.getNow(null).getView().findViewById(R.id.calories);
        calories.setText("123 Calories");

        allergensNode.setRenderable(allergens.getNow(null));

        TextView allergensTextView = allergens.getNow(null).getView().findViewById(R.id.allergens_header);
        allergensTextView.setText(Utils.allergens[3]);

    });
  }

  public AugmentedImage getImage() {
    return image;
  }
}

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
import android.util.Log;
import android.widget.TextView;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

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
  private static CompletableFuture<ViewRenderable> burgerDes, pizzaDes, sushiDes, riceDes;

  private Node currentFood;


  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
    if (ulCorner == null) {
      ulCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_upper_left.sfb"))
              .build();
      urCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_upper_right.sfb"))
              .build();
      llCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_lower_left.sfb"))
              .build();
      lrCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_lower_right.sfb"))
              .build();
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

      burgerDes =
              ViewRenderable.builder()
                      .setView(context, R.layout.burger)
                      .build();
      pizzaDes =
              ViewRenderable.builder()
                      .setView(context, R.layout.pizza)
                      .build();
      sushiDes =
              ViewRenderable.builder()
                      .setView(context, R.layout.sushi)
                      .build();
      riceDes =
              ViewRenderable.builder()
                      .setView(context, R.layout.rice)
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
    if (!ulCorner.isDone() || !urCorner.isDone() || !llCorner.isDone() || !lrCorner.isDone() || !hamburger.isDone()) {
      CompletableFuture.allOf(ulCorner, urCorner, llCorner, lrCorner, hamburger)
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
    Node cornerNode;

    // Upper left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(ulCorner.getNow(null));


    // Upper right corner.
    localPosition.set(0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(urCorner.getNow(null));

    // Lower right corner.
    localPosition.set(0.5f * image.getExtentX(), 0.0f, 0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(lrCorner.getNow(null));

    // Lower left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, 0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(llCorner.getNow(null));

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

    localPosition.set(0.0f, 0.0f, -1f * image.getExtentZ());
    Node detailsNode = new Node();
    detailsNode.setParent(this);
    detailsNode.setLocalPosition(localPosition);
    detailsNode.setLocalScale(new Vector3(0.25f,0.25f,0.25f));

    //current food
    localPosition.set(-1f * image.getExtentX(), 0.0f, 0);
    currentFood = new Node();
    currentFood.setParent(this);
    currentFood.setLocalPosition(localPosition);

    buttonNode.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,1,0,1));
        currentFood.setRenderable(sushi.getNow(null));
        currentFood.setLocalScale(new Vector3(0.25f,0.25f,0.25f));


        detailsNode.setRenderable(sushiDes.getNow(null));

    });

    buttonNode2.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,0,0,0));
        currentFood.setRenderable(hamburger.getNow(null));
        currentFood.setLocalScale(new Vector3(0.070f,0.070f,0.070f));

        detailsNode.setRenderable(burgerDes.getNow(null));
    });

    buttonNode3.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,0,0,0));
        currentFood.setRenderable(pizza.getNow(null));
        currentFood.setLocalScale(new Vector3(0.032f,0.032f,0.032f));

        detailsNode.setRenderable(pizzaDes.getNow(null));

    });

    buttonNode4.setOnTapListener((hitTestResult, motionEvent) -> {
        currentFood.setLocalRotation(new Quaternion(0,0,0,0));
        currentFood.setRenderable(rice.getNow(null));
        currentFood.setLocalScale(new Vector3(0.14f,0.14f,0.14f));

        detailsNode.setRenderable(riceDes.getNow(null));
    });


  }

  public AugmentedImage getImage() {
    return image;
  }
}

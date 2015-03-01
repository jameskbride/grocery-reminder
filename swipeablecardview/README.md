Android SwipeableRecyclerView
=====================

Based on [romannurik`s Android-SwipeToDismiss](https://github.com/romannurik/Android-SwipeToDismiss).

Sample project implementation of a list of `CardView`s in a `RecyclerView` with a `TouchListener` that allows dismissing of elements by swiping the elements to the left or right.

![Output sample](https://raw.githubusercontent.com/brnunes/SwipeableRecyclerView/master/demo.gif)

####How to use
- Copy the class [`SwipeableRecyclerViewTouchListener`](https://github.com/brnunes/SwipeableRecyclerView/blob/master/app/src/main/java/brnunes/swipeablecardview/SwipeableRecyclerViewTouchListener.java) to your project.
- Instantiate a `SwipeableRecyclerViewTouchListener` passing as parameters the `RecyclerView` and a `SwipeableRecyclerViewTouchListener.SwipeListener` that will receive the callbacks.
- Add the instantiated `SwipeableRecyclerViewTouchListener` as a [`RecyclerView.OnItemTouchListener`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.OnItemTouchListener.html).


```java
SwipeableRecyclerViewTouchListener swipeTouchListener =
        new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipe(int position) {
                        return true;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            mItems.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            mItems.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

mRecyclerView.addOnItemTouchListener(swipeTouchListener);
````

####Dependencies

The `RecyclerView` and `CardView` widgets are part of the [v7 Support Libraries](https://developer.android.com/tools/support-library/features.html#v7). To use these widgets in your project, add these Gradle dependencies to your app's module:

````
dependencies {
    ...
    compile 'com.android.support:cardview-v7:21.0.+'
    compile 'com.android.support:recyclerview-v7:21.0.+'
}
````
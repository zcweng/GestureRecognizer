# GestureRecognizer
Gesture Recognizer FOR Android

单指手势支持：点击、滑动
多指手势支持：旋转、缩放

使用方法：

        gestureRecognizer = GestureRecognizer.get(context);
        gestureRecognizer.addListener(this);
  
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return gestureRecognizer.onTouchEvent(event);
        }
        
        @Override
        public void onClick(float x, float y) {
        }

        @Override
        public void onScroll(float sx, float sy, float ex, float ey) {
        }

        @Override
        public void onRotate(double angle, float cx, float cy) {
        }

        @Override
        public void onScale(double scale, float cx, float cy) {
        }

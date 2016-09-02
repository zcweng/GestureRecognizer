# GestureRecognizer
Gesture Recognizer FOR Android


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

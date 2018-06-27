#!/bin/sh
#create symbolic links for libopencv_*.so files in /usr/lib/x86_64-linux-gnu to link: /usr/lib
OPEN_CV_INSTALL=/usr/lib/x86_64-linux-gnu
LIB_INSTALL=/usr/lib

sudo ln -s  $OPEN_CV_INSTALL/libopencv_calib3d.so        $LIB_INSTALL/libopencv_calib3d.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_contrib.so        $LIB_INSTALL/libopencv_contrib.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_core.so           $LIB_INSTALL/libopencv_core.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_features2d.so     $LIB_INSTALL/libopencv_features2d.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_flann.so          $LIB_INSTALL/libopencv_flann.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_gpu.so            $LIB_INSTALL/libopencv_gpu.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_highgui.so        $LIB_INSTALL/libopencv_highgui.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_imgproc.so        $LIB_INSTALL/libopencv_imgproc.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_legacy.so         $LIB_INSTALL/libopencv_legacy.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_ml.so             $LIB_INSTALL/libopencv_ml.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_objdetect.so      $LIB_INSTALL/libopencv_objdetect.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_ocl.so            $LIB_INSTALL/libopencv_ocl.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_photo.so          $LIB_INSTALL/libopencv_photo.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_stitching.so      $LIB_INSTALL/libopencv_stitching.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_superres.so       $LIB_INSTALL/libopencv_superres.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_ts.so             $LIB_INSTALL/libopencv_ts.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_video.so          $LIB_INSTALL/libopencv_video.so
sudo ln -s  $OPEN_CV_INSTALL/libopencv_videostab.so      $LIB_INSTALL/libopencv_videostab.so


ls -la $LIB_INSTALL/libopencv*

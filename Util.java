package com.pierce;

import java.io.Closeable;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by izhaowo on 16/8/31.
 */
public class Util {
    public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String md5(String str){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        messageDigest.update(str.getBytes());
        return toHexString(messageDigest.digest());
    }


    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }


    public static void close(Closeable closeable){
        if(closeable == null){return;}
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static float distanceSquare(float x1, float y1, float x2, float y2) {
        return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
    }

    /**求直线与X正半轴的夹角
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double getAngle(float x1, float y1, float x2, float y2) {
        final float px = x2-x1, py = y2-y1;
        final float c2 = distanceSquare(px, py, 0, 0);
        final float px2 = px*px;

        final double acos = Math.acos(Math.sqrt(1.0*px2/c2));
        final double degrees = Math.toDegrees(acos);

        return px > 0 ? (py >= 0 ? degrees : -degrees) : 180 - (py >= 0 ? degrees : -degrees);
    }

    /**
     * c^2+b^2-2cbcosA=a^2
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @param cx
     * @param cy
     * @return degree of A, >0
     */
    public static double getAngleA(float ax, float ay, float bx, float by, float cx, float cy) {
        double angle = getAngle(ax, ay, bx, by) - getAngle(ax, ay, cx, cy);
        return angle > 0 ? angle : -angle;

        //c^2+b^2-2cbcosA=a^2
//        final float c2 = distanceSquare(ax,ay,bx,by);
//        final float b2 = distanceSquare(ax,ay,cx,cy);
//        final float a2 = distanceSquare(cx,cy,bx,by);
//        final double bc = Math.sqrt(b2*c2);// * Math.sqrt(c2);
//        double cosA = (c2 + b2 - a2)/(2*bc);
//        if(cosA > 1.0){
//            cosA = 1.0;
//        }else if(cosA < -1.0){
//            cosA = -1.0;
//        }
//        final double acos = Math.acos(cosA);
//        return Math.toDegrees(acos);
    }
}

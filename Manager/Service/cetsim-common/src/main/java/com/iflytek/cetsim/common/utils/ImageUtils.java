package com.iflytek.cetsim.common.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * <b>类 名：</b>ImageUtils<br/>
 * <b>类描述：</b>图像操作工具<br/>
 * <b>创建人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>创建时间：</b>2016年9月12日 下午5:37:42<br/>
 * <b>修改人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>修改时间：</b>2016年9月12日 下午5:37:42<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 *
 */
public class ImageUtils {
	 /**  
     * 实现图像的等比缩放  
     * 
     * @param source  
     * @param targetW  
     * @param targetH  
     * @return  
     */  
    private static BufferedImage resize(BufferedImage source, int targetW,   
            int targetH) {   
        // targetW，targetH分别表示目标长和宽   
        int type = source.getType();
        BufferedImage target = null;   
        double sx = (double) targetW / source.getWidth();   
        double sy = (double) targetH / source.getHeight();   
        // 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放   
        // 则将下面的if else语句注释即可   
        if (sx < sy) {   
            sx = sy;   
            targetW = (int) (sx * source.getWidth());   
        } else {   
            sy = sx;   
            targetH = (int) (sy * source.getHeight());   
        }   
        if (type == BufferedImage.TYPE_CUSTOM) { // handmade   
            ColorModel cm = source.getColorModel();   
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW,   
                    targetH);   
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();   
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);   
        } else  
            target = new BufferedImage(targetW, targetH, type);   
        Graphics2D g = target.createGraphics();   
        // smoother than exlax:   
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);   
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));   
        g.dispose();   
        return target;   
    }   
  
    /**  
     * 实现图像的等比缩放和缩放后的截取  
     * @param inFilePath 要截取文件的路径  
     * @param outFilePath 截取后输出的路径  
     * @param width 要截取宽度  
     * @param hight 要截取的高度  
     * @param proportion  
     * @throws Exception  
     */  
       
    public static String saveImageAsJpg(String inFilePath, String outFilePath,   
            int width, int height, boolean proportion)throws Exception {   
         File file = new File(inFilePath);   
         InputStream in = new FileInputStream(file);   
         File saveFile = new File(outFilePath);   
        BufferedImage srcImage = ImageIO.read(in);   
        if (width > 0 || height > 0) {   
            // 原图的大小   
            int sw = srcImage.getWidth();   
            int sh = srcImage.getHeight();               
            // 如果原图像的大小小于要缩放的图像大小，直接将要缩放的图像复制过去   
            if (sw > width && sh > height) {
                srcImage = resize(srcImage, width, height);   
            } else {   
                String fileName = saveFile.getName();   
                String formatName = fileName.substring(fileName   
                        .lastIndexOf('.') + 1);   
                ImageIO.write(srcImage, formatName, saveFile);   
                return srcImage.getWidth() + "x" + srcImage.getHeight();   
            }   
        }   
        // 缩放后的图像的宽和高   
        int w = srcImage.getWidth();   
        int h = srcImage.getHeight();
        
        // 如果缩放后的图像和要求的图像宽度一样，就对缩放的图像的高度进行截取   
        if (w == width) {   
            // 计算X轴坐标   
            int x = 0;   
            int y = h / 2 - height / 2;   
            saveSubImage(srcImage, new Rectangle(x, y, width, height), saveFile);   
        }   
        // 否则如果是缩放后的图像的高度和要求的图像高度一样，就对缩放后的图像的宽度进行截取   
        else if (h == height) {   
            // 计算X轴坐标   
            int x = w / 2 - width / 2;   
            int y = 0;   
            saveSubImage(srcImage, new Rectangle(x, y, width, height), saveFile);   
        }   
        in.close();
        
        
        InputStream ins = new FileInputStream(saveFile);   
        BufferedImage destImage = ImageIO.read(ins);
        return srcImage.getWidth() + "x" + srcImage.getHeight();   
    }   
    /**  
     * 实现缩放后的截图  
     * @param image 缩放后的图像  
     * @param subImageBounds 要截取的子图的范围  
     * @param subImageFile 要保存的文件  
     * @throws IOException  
     */  
    private static void saveSubImage(BufferedImage image,   
            Rectangle subImageBounds, File subImageFile) throws IOException {   
        if (subImageBounds.x < 0 || subImageBounds.y < 0  
                || subImageBounds.width - subImageBounds.x > image.getWidth()   
                || subImageBounds.height - subImageBounds.y > image.getHeight()) {   
            System.out.println("Bad   subimage   bounds");   
            return;   
        }   
        BufferedImage subImage = image.getSubimage(subImageBounds.x,subImageBounds.y, subImageBounds.width, subImageBounds.height);   
        String fileName = subImageFile.getName();   
        String formatName = fileName.substring(fileName.lastIndexOf('.') + 1);   
        ImageIO.write(subImage, formatName, subImageFile);   
    }
    
	/**
	 * 生成背景图片
	 * 
	 * @param g
	 * @return 
	 */
	public static void createBackground(Graphics g,int w,int h) {
		// 填充背景
		g.setColor(getRandColor(220,250)); 
		g.fillRect(0, 0, w, h);
		// 加入干扰线条
		for (int i = 0; i < 8; i++) {
			g.setColor(getRandColor(40,150));
			Random random = new Random();
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			int x1 = random.nextInt(w);
			int y1 = random.nextInt(h);
			g.drawLine(x, y, x1, y1);
		}
	}
	
	/**
	 * 获取随机色
	 * 
	 * @param fc
	 * @param bc
	 * @return 随机色
	 */
	private static Color getRandColor(int fc,int bc) { 
		int f = fc;
		int b = bc;
		Random random=new Random();
        if(f>255) {
        	f=255; 
        }
        if(b>255) {
        	b=255; 
        }
        return new Color(f+random.nextInt(b-f),f+random.nextInt(b-f),f+random.nextInt(b-f)); 
	}
}

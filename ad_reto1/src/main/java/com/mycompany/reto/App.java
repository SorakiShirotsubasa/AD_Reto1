/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.reto;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static jdk.nashorn.tools.ShellFunctions.input;

/**
 *
 * @author javie
 */
public class App extends JFrame {

    public static Scanner entrada = new Scanner(System.in);

    /**
     * Este metodo es parte de el main y nuestro menu
     *
     * @param opc
     */
    public static void opcion(int opc) {
        switch (opc) {
            case 1:
                mapeo();
                break;
            case 2:
                //No lo he hecho
                break;
            case 3:
                //No lo he hecho
                break;
            case 4:
                System.out.println("Donde se encuentra la imagen que quieres redimensionar?");
                String origen = entrada.nextLine();
                System.out.println("A que escala quieres redimensionarla? (Ejemplo:10:200)");
                String escala = entrada.nextLine();
                String[] datos = escala.split(":");
                int ancho = Integer.parseInt(datos[0]),
                 alto = Integer.parseInt(datos[1]);

                recortar(origen, ancho, alto);
                break;
            case 5:
                break;
            default:
                System.out.println("Introduce una opcion valida");
                break;
        }
    }

    /**
     * Este metodo es el que se encarga de buscar dentro de un directorio todas
     * las imagenes y sacar los datos de cada una
     */
    public static void mapeo() {
        System.out.println("Especifique la ruta origen de sus fotos");
        String origen = entrada.nextLine();
        File ruta = new File(origen);
        File[] listaImg = ruta.listFiles();
        for (int i = 0; i < listaImg.length; i++) {
            try {
                System.out.println(listaImg[i].getName());

                File foto = new File(listaImg[i].getAbsolutePath());

                Metadata datos = ImageMetadataReader.readMetadata(foto);
                ExifSubIFDDirectory directory = datos.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

                Date fecha = directory.getDateOriginal();
                String formato = "MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(formato);
                String calendar = sdf.format(fecha);

                File destino = new File(calendar);
                if (!destino.exists()) {
                    destino.mkdir();
                }

                File copia = new File(destino.getAbsolutePath() + "/" + foto.getName());
                InputStream fis = new FileInputStream(foto);
                OutputStream out = new FileOutputStream(copia);
                byte[] buf = new byte[fis.available()];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                fis.close();
                out.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ImageProcessingException | IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     * Este es el metodo que se encarga dada una ruta el ancho y el alto de una
     * imagen de redimensionarla
     *
     * @param ruta
     * @param ancho
     * @param alto
     */
    public static void recortar(String ruta, int ancho, int alto) {

        File archivo1 = new File(ruta);
        String ruta2 = archivo1.getAbsolutePath();
        ruta2 = ruta2.substring(0, ruta2.length() - 4) + "Reducida" + ruta2.substring(ruta2.length() - 4, ruta2.length());
        File archivo2 = new File(ruta2);
        try {
            if (archivo1.exists()) {
                archivo2.createNewFile();
                BufferedImage src = ImageIO.read(archivo1);
                BufferedImage dest = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

                Graphics2D g = dest.createGraphics();
                AffineTransform at = AffineTransform.getScaleInstance((double) ancho / src.getWidth(), (double) alto / src.getHeight());

                g.drawRenderedImage(src, at);

                ImageIO.write(dest, "JPG", archivo2);

            } else {
                System.out.println("No se pudo encontrar la imagen");
            }
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * El menu parte del main
     */
    public static void menu() {
        System.out.println("1.-Seleccionar directorio"
                + "\n2.-Info coordenadas"
                + "\n3.-Carpetas segun la ciudad"
                + "\n4.-Reduccion de las fotos"
                + "\n5.-Salir");
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        int opc = 0;
        while (opc != 5) {
            menu();
            opc = Integer.parseInt(entrada.nextLine());
            opcion(opc);
        }

    }

}

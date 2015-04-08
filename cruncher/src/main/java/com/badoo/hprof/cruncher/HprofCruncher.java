package com.badoo.hprof.cruncher;

import com.badoo.hprof.library.HprofReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Application for converting HPROF files to the BMD format.
 * <p/>
 * In this process some data is lost:
 * <p/>
 * - Most strings (strings are replaced with hashes that can potentially be reversed with access to the app that generated the dump).
 * - Primitive instance fields.
 * - Stack trace serial numbers and protection domain for instance dumps.
 * <p/>
 * Created by Erik Andre on 22/10/14.
 */
public class HprofCruncher {

    /**
     * Crunch a HPROF file, converting it to BMD format.
     *
     * @param inFile Input file (hprof)
     * @param out    Output (BMD)
     * @throws IOException If an error occurs while writing the output data
     */
    public static void crunch(File inFile, OutputStream out) throws IOException {
        CrunchProcessor processor = new CrunchProcessor(out);
        // Start first pass
        InputStream in = new BufferedInputStream(new FileInputStream(inFile));
        HprofReader reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
        }
        processor.startSecondPass();
        in.close();
        // Start second pass
        in = new BufferedInputStream(new FileInputStream(inFile));
        reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
        }
        processor.finishAndWriteOutput();
    }

    public static void main(String[] args) {
        String inFile;
        String outFile;
        if (args != null && args.length >= 2) {
            inFile = args[0];
            outFile = args[1];
        }
        else {
            inFile = "in.hprof";
            outFile = "out.bmd";
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            crunch(new File(inFile), out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

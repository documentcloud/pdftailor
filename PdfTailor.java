import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
 
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;

public class PdfTailor {
  
  static class UnstitchCommand {
    @Parameter(description = "The file to unstitch.")
    private List<String> files;

    @Parameter(names = {"--output", "-o"}, description = "The filename pattern to write to. (e.g. mydir/file_%d_name.pdf)", required = true)
    private String output;
  }
  
  static class StitchCommand {
    @Parameter(description = "The list of files to stitch together.")
    private List<String> files;
    
    @Parameter(names = {"--output", "-o"}, description = "The filename to write to.", required = true)
    private String output;
  }
  
  // Class Runner
  public static void main( String[] args ) throws IOException, DocumentException {
    StitchCommand   stitch   = new StitchCommand();
    UnstitchCommand unstitch = new UnstitchCommand();
    JCommander cli = new JCommander();
    
    cli.addCommand("stitch",   stitch);
    cli.addCommand("unstitch", unstitch);

    if (args.length == 0) { 
      usage(); 
    } else {
      try { 
        cli.parse(args);

        String command = cli.getParsedCommand();
        if      (command.equals("stitch"))   { stitch(stitch); } 
        else if (command.equals("unstitch")) { unstitch(unstitch); }
        else                                 { usage(); }
        
      } catch (MissingCommandException unrecognizedCommand) {
        usage();
      }
    }
  }
  
  public static String USAGE_MESSAGE = "pdftailor stitches and unstitches pdfs.\n\n"                        +
                                       "Usage:\n"                                                           +
                                       "  pdftailor COMMAND [OPTIONS] <pdf(s)>\n"                           +
                                       "  Main commands:\n"                                                 +
                                       "    stitch, unstitch\n\n"                                           +
                                       "Options:\n"                                                         +
                                       "  -o, --output\n"                                                   +
                                       "    The file name or file pattern to which output is written.\n"    +
                                       "    For commands like unstitch which will write multiple files\n"   +
                                       "    a pattern including \"%d\" can be used to specify a template\n" +
                                       "    for where files should be written (e.g. ./foo/bar_%d.pdf).\n\n" +
                                       "Example:\n"                                                         +
                                       "  pdftailor stitch --output merged.pdf a.pdf b.pdf\n"               +
                                       "  pdftailor unstitch --output merged_page_%d.pdf merged.pdf\n";

  public static void usage() {
    System.out.println(USAGE_MESSAGE);
  }

  public static void stitch( StitchCommand cli ) throws IOException, DocumentException {
    String outputName = cli.output;

    Document document = new Document();
    PdfCopy writer = new PdfCopy(document, new FileOutputStream(outputName));
    document.open();
    
    PdfReader reader;
    Iterator<String> pdfPaths = cli.files.iterator();
    while (pdfPaths.hasNext()) {
      String path = pdfPaths.next();
      reader = new PdfReader(path);
      for ( int pageNumber = 0; pageNumber < reader.getNumberOfPages(); ) {
        writer.addPage(writer.getImportedPage( reader, ++pageNumber ));
      }
      writer.freeReader(reader);
    }
    document.close();
  }
  
  public static void unstitch( UnstitchCommand cli ) throws IOException, DocumentException {
    String readerPath = cli.files.get(0);
    PdfReader reader = new PdfReader(readerPath);
    for ( int pageNumber = 0; pageNumber < reader.getNumberOfPages(); ) {
      pageNumber++;
      Document document = new Document();
      PdfCopy writer = new PdfCopy(document, new FileOutputStream(outputPath(cli.output, pageNumber)));

      document.open();
      writer.addPage(writer.getImportedPage(reader, pageNumber));
      document.close();
      writer.close();
    }
  }
  
  protected static String outputPath( String outputName, int pageNumber ) {
    Pattern templatePattern = Pattern.compile("%d");
    Pattern filePattern = Pattern.compile("^(.+)\\.pdf$");
    
    String path;
    if ( templatePattern.matcher(outputName).find() ){
      path = String.format(outputName, pageNumber);
    } else if ( filePattern.matcher(outputName).find() ) {
      path = filePattern.matcher(outputName).replaceFirst("$1_" + pageNumber + ".pdf");
    } else {
      path = outputName + "_" + pageNumber + ".pdf";
    }
    return path;
  }
}

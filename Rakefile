desc "cleanup intermediate compilation files"
task :cleanup do
  puts 'cleaning up'
  FileUtils.rm Dir.glob("./java/src/org/documentcloud/pdftailor/*.class")
end

desc "Compile Java sources"
task :compile do
  puts "compiling..."
  `javac -Xlint:unchecked -classpath #{jars} #{sources}`
end

desc "Compile Java sources and create the pdftailor.jar package"
task :jar => [:cleanup, :compile] do
  `cd java/src/; jar cvf ../../jars/pdftailor.jar org/documentcloud/pdftailor/*.class`
end

def jars(prefix = nil)
  Dir.open("jars").select{ |j| j =~ /jar$/ }.map{ |jar| "#{ prefix + "/" if prefix }jars/#{jar}" }.join(":")
end

def sources
  %w(java/src/org/documentcloud/pdftailor/PdfTailor.java).join(" ")
end

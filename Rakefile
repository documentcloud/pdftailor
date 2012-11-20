task :compile do
  `javac -Xlint:unchecked -classpath #{jars} #{sources}`
end

task :jar do
  `cd java/src/; jar cvf ../../jars/pdftailor.jar org/documentcloud/pdftailor/*.class`
end

def jars(prefix = nil)
  Dir.open("jars").select{ |j| j =~ /jar$/ }.map{ |jar| "#{ prefix + "/" if prefix }jars/#{jar}" }.join(":")
end

def sources
  %w(java/src/org/documentcloud/pdftailor/PdfTailor.java).join(" ")
end

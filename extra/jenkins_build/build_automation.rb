
require 'fileutils'

def uploadArchives() 
  puts ".................................couchbase-lite-java-core"
  uploadArchivesSingleLibrary("libraries:couchbase-lite-java-core",       "build",    "")
  
  puts ".................................couchbase-lite-android"
  uploadArchivesSingleLibrary("libraries:couchbase-lite-android",         "build",    "buildAndroidWithArtifacts")
  
  puts ".................................couchbase-lite-java-javascript"
  uploadArchivesSingleLibrary("libraries:couchbase-lite-java-javascript", "assemble", "buildJavascriptWithArtifacts")
  
  puts ".................................couchbase-lite-java-listener"
  uploadArchivesSingleLibrary("libraries:couchbase-lite-java-listener",   "assemble", "buildListenerWithArtifacts")

end

# upload the archives for a single library,
def uploadArchivesSingleLibrary(libraryName, buildCommand, systemProperty)
  
  cmd = "./gradlew :#{libraryName}:#{buildCommand} --debug"
  cmd = "#{cmd} -D#{systemProperty}" if !systemProperty.empty?
  puts "-------------------------------------------------------"
  puts cmd
  runCommandCheckError cmd
  
  cmd = "./gradlew :#{libraryName}:uploadArchivesWrapper --debug"
  cmd = "#{cmd} -D#{systemProperty}" if !systemProperty.empty?
  puts "-------------------------------------------------------"
  puts cmd
  runCommandCheckError cmd
  puts "-------------------------------------------------------"

end 

def clean() 
  cmd = "./gradlew clean"
  puts cmd
  build_result = %x( #{cmd} )
  puts build_result

end

def buildCode() 
  cmd = "./gradlew assemble"
  puts cmd
  build_result = %x( #{cmd} )
  puts build_result
  # check if the build worked 
  if ($?.exitstatus != 0) 
    puts "Build error, aborting"
    exit($?.exitstatus)
  end
end

def build() 
  # make sure we are in the correct place
  assertPresentInCurrentDirectory(["settings.gradle"])

  # build the code
  puts "Building .."
  build_result = buildCode()
  puts "Build result: #{build_result}"


end

def assertPresentInCurrentDirectory(file_list) 

  Dir.foreach('.') do |item|
    next if item == '.' or item == '..'
    if file_list.include? item 
      file_list.delete item
    end
  end

  raise "Did not find all %s in current dir" % file_list if file_list.size() != 0

end

def runCommand(cmd)
  puts cmd 
  result = %x( #{cmd} )
  puts result
end

def runCommandCheckError(cmd)
  puts cmd 
  result = %x( #{cmd} )
  puts result

  if ($?.exitstatus != 0) 
    puts "Error, aborting"
    exit($?.exitstatus)
  end

end



require 'fileutils'

def uploadArchives() 
  uploadArchivesSingleLibrary("libraries:couchbase-lite-java-core", "build", "")
  uploadArchivesSingleLibrary("libraries:couchbase-lite-android", "build", "buildAndroidWithArtifacts")
  uploadArchivesSingleLibrary("libraries:couchbase-lite-java-javascript", "assemble", "buildJavascriptWithArtifacts")
  uploadArchivesSingleLibrary("libraries:couchbase-lite-java-listener", "assemble", "buildListenerWithArtifacts")

end

# upload the archives for a single library,
def uploadArchivesSingleLibrary(libraryName, buildCommand, systemProperty)
  
  cmd = "./gradlew :#{libraryName}:#{buildCommand}"
  cmd = "#{cmd} -D#{systemProperty}" if !systemProperty.empty?
  runCommandCheckError cmd

  cmd = "./gradlew :#{libraryName}:uploadArchivesWrapper"
  cmd = "#{cmd} -D#{systemProperty}" if !systemProperty.empty?
  runCommandCheckError cmd

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


def buildZipArchiveRelease() 
  
  android_VERSION  = ENV["VERSION"]
  android_REVISION = ENV["REVISION"]
  thirdPartyArchive    = "com.couchbase.cblite-#{android_REVISION}"
  thirdPartyZipArchive = "#{thirdPartyArchive}-android.zip"
  localArchive         = "cblite_android_#{android_REVISION}"
  localZipArchive      = "#{localArchive}.zip"
  
  # create localarchive directory
  runCommand "mkdir #{localArchive}"

  # download 3rd party jars into a zip file
  runCommand "cd    release && ./zip_jars.sh #{android_REVISION}"
  runCommand "file  release/target/#{thirdPartyZipArchive} || exit 99"
  runCommand "cp    release/target/#{thirdPartyZipArchive} ."
  
  # unzip it
  runCommand "unzip #{thirdPartyZipArchive}"
  
  # rename it
  runCommand "mv #{thirdPartyArchive} #{localArchive}"
  
  # collect the new cblite jar files and name them correctly based on UPLOAD_VERSION_CBLITE env var

  modulesToPaths = {
    "couchbase-lite-java-core" => "libraries/couchbase-lite-java-core/build/libs/couchbase-lite-java-core.jar",
    "couchbase-lite-android" => "libraries/couchbase-lite-android/build/bundles/release/classes.jar",
    "couchbase-lite-java-javascript" => "libraries/couchbase-lite-java-javascript/build/libs/couchbase-lite-java-javascript.jar",
    "couchbase-lite-java-listener" => "libraries/couchbase-lite-java-listener/build/libs/couchbase-lite-java-listener.jar"
  }

  modulesToPaths.each { |mod, srcPath| 
    envVarName = "UPLOAD_VERSION_CBLITE"
    if mod == "couchbase-lite-java-javascript"
      envVarName = "UPLOAD_VERSION_CBLITE_JAVASCRIPT"
    elsif mod == "couchbase-lite-java-listener"
      envVarName = "UPLOAD_VERSION_CBLITE_LISTENER"
    end
    envVarValue = ENV[envVarName]
    dest = "#{localArchive}/#{mod}-#{envVarValue}.jar"     
    cmd = "cp #{srcPath} #{dest}"
    runCommand cmd
  }
  
  # re-zip the zip file and put in current directory  
  runCommand "zip -r --junk-paths #{localZipArchive} #{localArchive}"
  
end

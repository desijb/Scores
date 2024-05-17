node {
  stage("Clone project") {
	print 'Cloning...'
    git branch: 'main', url: 'https://github.com/desijb/Scores.git'
  }

  stage("Build project without test execution") {
	print 'Building...'
    sh "/var/jenkins_home/maven/bin/mvn clean install -DskipTests"
  }
}
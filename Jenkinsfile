node {
  stage("Clone project") {
	print 'Cloning...'
    git branch: 'main', url: 'https://github.com/desijb/Scores.git'
  }

  stage("Build project without test execution") {
	print 'Building...'
    sh "mvn -Dmaven.test.skip=true  package"
  }
}
node {
  stage("Clone project") {
    git branch: 'main', url: 'https://github.com/desijb/Scores.git'
  }

  stage("Build project with test execution") {
    sh "mvn -Dmaven.test.skip=true  package"
  }
}
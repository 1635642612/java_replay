pipeline {
agent any
stages {
    stage('拉取代码开始啦！！！！！！！！！') {
    steps {
    checkout([$class: 'GitSCM', branches: [[name: '*/master']],
    doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [],
    userRemoteConfigs: [[credentialsId: 'ff76af73-81cc-444f-9348-4788eb820c79', url:
    'git@github.com:1635642612/java_replay.git']]])
    }
}
stage('编译构建！！！！！！！！！') {
    steps {
        sh label: '', script: 'mvn clean package'
    }
}
stage('项目部署！！！！！！！！！') {
    /* steps {
        deploy adapters: [tomcat8(credentialsId: 'afc43e5e-4a4e-4de6-984fb1d5a254e434', path: '', url: 'http://192.168.66.102:8080')], contextPath: null,
        war: 'target *//*.war'
      } */
    }
  }
}
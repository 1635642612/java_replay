pipeline {
    agent any
stages {

        stage('start fetch v2 code v2 branchs!!!') {
             steps {
                 checkout([$class: 'GitSCM', branches: [[name: '*/${branch}']],
                 doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [],
                 userRemoteConfigs: [[credentialsId: 'ff76af73-81cc-444f-9348-4788eb820c79', url:
                 'git@github.com:1635642612/java_replay.git']]])
             }
         }

         stage('start build !!!') {
               steps {
                     echo '开始构建'
               }

         }

         stage('project deploy !!!') {
                steps {
                       echo '开始部署'
                   }
          }

   }

}
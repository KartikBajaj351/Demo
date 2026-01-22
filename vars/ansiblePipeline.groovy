def call(Map config = [:]) {

    pipeline {
        agent any

        environment {
            ANSIBLE_FORCE_COLOR = 'true'
            ANSIBLE_HOST_KEY_CHECKING = 'False'
        }

        stages {

            stage('Checkout Code') {
                steps {
                    checkout scm
                }
            }

            stage('Verify Project') {
                steps {
                    dir(config.workDir ?: 'prod') {
                        sh '''
                        pwd
                        ls -l
                        '''
                    }
                }
            }

            stage('Ansible Syntax Check') {
                steps {
                    dir(config.workDir ?: 'prod') {
                        sh """
                        ansible-playbook ${config.playbook ?: 'redis.yml'} \
                          -i ${config.inventory ?: 'inventory'} \
                          --syntax-check
                        """
                    }
                }
            }

            stage('Run Ansible Playbook') {
                steps {
                    dir(config.workDir ?: 'prod') {
                        sh """
                        ansible-playbook ${config.playbook ?: 'redis.yml'} \
                          -i ${config.inventory ?: 'inventory'}
                        """
                    }
                }
            }
        }
    }
}

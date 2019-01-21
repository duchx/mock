##################################################################
# deploy script by hongwm
# since 2015-06-01
##################################################################
DIR=`dirname $0`;
PKG_FILE=$1;
DEPLOY_MACHINE=$2;
PORT=$3

if [ $# -ne 3 ]
then
    echo deploy.sh pkg_file machine/list port
    exit -1
fi

tomcat_dir="${HOME}/tomcat/tomcat_$PORT";

if [ ! -f $PKG_FILE ]
then
    echo "$PKG_FILE not exists"
    exit -1;
fi

function ensure_tomcat() {
    machine=$1
    scp $DIR/setup_tomcat.sh $machine:/tmp/
    ssh $machine "sh /tmp/setup_tomcat.sh $PORT"
}

function deploy() {
    machine=$1;
    echo "start to deploy machine $machine";
    ensure_tomcat $machine;
    scp $PKG_FILE $machine:$tomcat_dir/webapps/ROOT.war
    ssh $machine "cd $tomcat_dir/; sh bin/shutdown.sh; sleep 1; ps auxwww | grep java | grep tomcat_$PORT | awk '{print \$2}' | xargs kill -9"
    ssh $machine "rm -rf $tomcat_dir/webapps/ROOT/; cd $tomcat_dir/; bin/startup.sh"
    echo "===================================================================================";
    echo "machine $machine deploy end";
}

if [ -f $DEPLOY_MACHINE ]
then
    for machine in `cat $DEPLOY_MACHINE`
    do  
        deploy $machine;
        sleep 2
    done
else
    deploy $DEPLOY_MACHINE;
fi


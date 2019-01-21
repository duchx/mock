if [ $# -ne 1 ]
then
    echo setup_tomcat.sh port
    exit -1;
fi
port=$1
tomcat_home="${HOME}/tomcat";
url="http://res.souche.com/tomcat.tar.gz"

tomcat_dir="${tomcat_home}/tomcat_$port"
echo $tomcat_dir
if [ ! -d $tomcat_dir ]
then
    wget $url -O /tmp/tomcat.tar.gz
    mkdir -p $tomcat_dir
    tar zxvf /tmp/tomcat.tar.gz -C $tomcat_dir
    http_port=$port
    shutdown_port=$((port+30000))
    ajp_port=$((port+40000))
    redirect_port=$((port+45678))
    sed -i "s/http.port=.*/http.port=${http_port}/g" $tomcat_dir/conf/catalina.properties
    sed -i "s/shutdown.port=.*/shutdown.port=${shutdown_port}/g" $tomcat_dir/conf/catalina.properties
    sed -i "s/ajp.port=.*/ajp.port=${ajp_port}/g" $tomcat_dir/conf/catalina.properties
    sed -i "s/redirect.port=.*/redirect.port=${redirect_port}/g" $tomcat_dir/conf/catalina.properties
fi

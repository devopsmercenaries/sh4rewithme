TARGET_DIR=jmxtrans-prod

rm ${TARGET_DIR}/*.json

#AS Monitoring
./generate-jmxtrans.sh jmxtrans/as ${TARGET_DIR} b2cA-app1:app1.courtanet.net:8892,b2cB-app1:app1.courtanet.net:8893,b2cC-app1:app1.courtanet.net:8894
./generate-jmxtrans.sh jmxtrans/as ${TARGET_DIR} b2cA-app2:app2.courtanet.net:8892,b2cB-app2:app2.courtanet.net:8893,b2cC-app2:app2.courtanet.net:8894
./generate-jmxtrans.sh jmxtrans/as ${TARGET_DIR} b2cA-app3:app3.courtanet.net:8892,b2cB-app3:app3.courtanet.net:8893,b2cC-app3:app3.courtanet.net:8894
./generate-jmxtrans.sh jmxtrans/benchmark ${TARGET_DIR} b2c-benchmark:ovh6.courtanet.net:5898

#JMETER Monitoring : 
#./generate-jmxtrans.sh jmxtrans/jmeter ${TARGET_DIR} jmeterA:gandi1.courtanet.net:8740,jmeterB:gandi1.courtanet.net:8741,jmeterC:gandi1.courtanet.net:8742,jmeterD:gandi1.courtanet.net:8743,jmeterE:gandi1.courtanet.net:8744,jmeterF:gandi1.courtanet.net:8745

#Setup EC2 Tooling
source ./ec2jenkins/scripts/setenv-ec2.sh

#JMETER EC2 Monitoring
NUM=1
for RUNNING_INSTANCE in `ec2-describe-instances | grep INSTANCE | awk '{print $4}' | grep ec2`
do
    ./generate-jmxtrans.sh jmxtrans/jmeter ${TARGET_DIR} jmeter-ec2-$NUM:${RUNNING_INSTANCE}:8740
    NUM=$((NUM+1))
done
#Show to preview the commit to be done
#svn st ${TARGET_DIR}

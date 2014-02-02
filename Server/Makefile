
all: smartcar_srv smartcar_srvd

smartcar_srv: udp_car_ctrl_server.c
	gcc -o  smartcar_srv udp_car_ctrl_server.c 

smartcar_srvd: udp_car_ctrl_server_daemon.c
	gcc -o  smartcar_srvd udp_car_ctrl_server_daemon.c 

clean: 
	rm -rf smartcar_srv smartcar_srvd

#include <errno.h>
#include <termios.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <inttypes.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <syslog.h>
#include <signal.h>

#define DEFAULT_PORT 5000

int
set_interface_attribs (int fd, int speed, int parity)
{
        struct termios tty;
        memset (&tty, 0, sizeof (tty));
        if (tcgetattr (fd, &tty) != 0)
        {
                syslog (LOG_NOTICE, "error %d from tcgetattr", errno);
                return -1;
        }

        cfsetospeed (&tty, speed);
        cfsetispeed (&tty, speed);

        tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;     // 8-bit chars
        // disable IGNBRK for mismatched speed tests; otherwise receive break
        // as \000 chars
        tty.c_iflag &= ~IGNBRK;         // ignore break signal
        tty.c_lflag = 0;                // no signaling chars, no echo,
                                        // no canonical processing
        tty.c_oflag = 0;                // no remapping, no delays
        tty.c_cc[VMIN]  = 0;            // read doesn't block
        tty.c_cc[VTIME] = 5;            // 0.5 seconds read timeout

        tty.c_iflag &= ~(IXON | IXOFF | IXANY); // shut off xon/xoff ctrl

        tty.c_cflag |= (CLOCAL | CREAD);// ignore modem controls,
                                        // enable reading
        tty.c_cflag &= ~(PARENB | PARODD);      // shut off parity
        tty.c_cflag |= parity;
        tty.c_cflag &= ~CSTOPB;
        tty.c_cflag &= ~CRTSCTS;

        if (tcsetattr (fd, TCSANOW, &tty) != 0)
        {
                syslog (LOG_NOTICE, "error %d from tcsetattr", errno);
                return -1;
        }
        return 0;
}

void
set_blocking (int fd, int should_block)
{
        struct termios tty;
        memset (&tty, 0, sizeof tty);
        if (tcgetattr (fd, &tty) != 0)
        {
                syslog (LOG_NOTICE, "error %d from tggetattr", errno);
                return;
        }

        tty.c_cc[VMIN]  = should_block ? 1 : 0;
        tty.c_cc[VTIME] = 5;            // 0.5 seconds read timeout

        if (tcsetattr (fd, TCSANOW, &tty) != 0)
                syslog (LOG_NOTICE, "error %d setting term attributes", errno);
}

int sockfd;

int init_sock(int port)
{
	struct sockaddr_in servaddr,cliaddr;

	sockfd=socket(AF_INET,SOCK_DGRAM,0);

        bzero(&servaddr,sizeof(servaddr));
        servaddr.sin_family = AF_INET;
        servaddr.sin_addr.s_addr=htonl(INADDR_ANY);
        servaddr.sin_port=htons(port);
        bind(sockfd,(struct sockaddr *)&servaddr,sizeof(servaddr));
        /* set socket timeout */
        struct timeval tv;
        tv.tv_sec = 5; /* if no input for 5secs, stop the car */
        tv.tv_usec = 0;
	if (setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO,&tv,sizeof(tv)) < 0) {
	    perror("Error");
	}
        return 0;
}


static void skeleton_daemon(char* daemon_name)
{
    pid_t pid;

    /* Fork off the parent process */
    pid = fork();

    /* An error occurred */
    if (pid < 0)
        exit(EXIT_FAILURE);

    /* Success: Let the parent terminate */
    if (pid > 0)
        exit(EXIT_SUCCESS);

    /* On success: The child process becomes session leader */
    if (setsid() < 0)
        exit(EXIT_FAILURE);

    /* Catch, ignore and handle signals */
    //TODO: Implement a working signal handler */
    signal(SIGCHLD, SIG_IGN);
    signal(SIGHUP, SIG_IGN);

    /* Fork off for the second time*/
    pid = fork();

    /* An error occurred */
    if (pid < 0)
        exit(EXIT_FAILURE);

    /* Success: Let the parent terminate */
    if (pid > 0)
        exit(EXIT_SUCCESS);

    /* Set new file permissions */
    umask(0);

    /* Change the working directory to the root directory */
    /* or another appropriated directory */
    chdir("/");

    /* Close all open file descriptors */
    int x;
    for (x = sysconf(_SC_OPEN_MAX); x>0; x--)
    {
        close (x);
    }

    /* Open the log file */
    openlog ("smartcar", LOG_PID, LOG_DAEMON);
}

int main(int argc, char *argv[])
{
        skeleton_daemon( argv[0]);

	//char *portname = "/dev/ttyUSB1";
	char *portname = "/dev/ttyACM0";
	int fd = open (portname, O_RDWR | O_NOCTTY | O_SYNC);
	if (fd < 0)
	{
		//printf ("error %d opening %s: %s\n", errno, portname, strerror (errno));
		syslog (LOG_NOTICE, "error %d opening %s: %s\n", errno, portname, strerror (errno));
	        char *portname2 = "/dev/ttyACM1";
                fd = open (portname2, O_RDWR | O_NOCTTY | O_SYNC);
	        if (fd < 0)
                {
		    //printf ("error %d opening %s: %s\n", errno, portname2, strerror (errno));
		    syslog (LOG_NOTICE, "error %d opening %s: %s\n", errno, portname2, strerror (errno));
                    return;
                }
	}
        

	set_interface_attribs (fd, B9600, 0);  // set speed to 115,200 bps, 8n1 (no parity)
	set_blocking (fd, 0);                // set no blocking
 
        int cnt = 0;
        uint8_t stp = 0;
        uint8_t fwd = 1;
        uint8_t bw  = 2;
        uint8_t right = 3;
        uint8_t left = 4;
        uint8_t straight = 5;
        
        /* * * * * * * * * *        
         * * * * * * * * * *        
         * * * * * * * * * *        
         * UDP server init *
         * * * * * * * * * *        
         * * * * * * * * * */
         
        int port = DEFAULT_PORT;
        if (argc == 2)
        {
           port = atoi(argv[1]);
        }
        if (init_sock(port) == 0 )
        {
		//printf(" Listening on port %d\n", port);
                syslog(LOG_NOTICE, " Listening on port %d", port);
        }
	else
        {
                //printf(" socket init failed\n");
                syslog (LOG_NOTICE, " socket init failed\n");
                exit(1);
        }
 
        char state[255] = "stop";       

        while(1)
	{
		char mesg[15] = {};
		socklen_t len;
	        struct sockaddr_in cliaddr;
                len = sizeof(cliaddr);
                if(recvfrom(sockfd, mesg,sizeof(mesg), 0,
			       (struct sockaddr *) &cliaddr, &len) < 0)
                {
			//timeout reached
			//printf("Timout reached. Sending Stop \n");
                        syslog (LOG_NOTICE, "Timout reached. Sending Stop");
			write (fd, &stp, 1); 
		}
                else
                {
			//syslog (LOG_NOTICE, "%s\n", mesg);
                        if (strcmp(mesg, "STOP") == 0)
                        {
                                //if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &stp, 1);
                                }
                        }
                        else if (strcmp(mesg, "LEFT") == 0)
                        {
                                //if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &left, 1);
                                }
                        }
                        else if (strcmp(mesg, "STRAIGHT") == 0)
                        {
                                //if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &straight, 1);
                                }
                        }
                        else if (strcmp(mesg, "RIGHT") == 0)
                        {
                                //if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &right, 1);
                                }
                        }
                        else if (strcmp(mesg, "FWD") == 0)
                        {
                                //if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &fwd, 1);
                                }
                        } 
                        else if (strcmp(mesg, "BACK") == 0)
                        {
                                //if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &bw, 1);
                                }
                        }
                        else if (strcmp(mesg, "FWDSTRAIGHT") == 0)
                        {
                                if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &fwd, 1);
					write (fd, &straight, 1);
                                }
                        }
                        else if (strcmp(mesg, "FWDRIGHT") == 0)
                        {
                                if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &fwd, 1);
					write (fd, &right, 1);
                                }
                        }
                        else if (strcmp(mesg, "FWDLEFT") == 0)
                        {
                                if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &fwd, 1);
					write (fd, &left, 1);
                                }
                        }
                        else if (strcmp(mesg, "BACKSTRAIGHT") == 0)
                        {
                                if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &bw, 1);
					write (fd, &straight, 1);
                                }
                        }
                        else if (strcmp(mesg, "BACKRIGHT") == 0)
                        {
                                if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &bw, 1);
					write (fd, &right, 1);
                                }
                        }
                        else if (strcmp(mesg, "BACKLEFT") == 0)
                        {
                                if (strcmp(state, mesg) != 0)
                                {
					syslog (LOG_NOTICE, "%s\n", mesg);
                                        strcpy(state, mesg);
					write (fd, &bw, 1);
					write (fd, &left, 1);
                                }
                        }
                }

	}
	write (fd, &stp, 1);           // send 7 character greeting

        close(fd);
}

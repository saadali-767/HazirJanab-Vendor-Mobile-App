1) add variable ENCRYPTION_KEY to environment variables and add 32-bit AES-256 key.

2) open mysql config and change:-
	wait_timeout = 300  # Increase to 5 minutes (300 seconds) or more
	max_allowed_packet = 16M  # Increase to 16 megabytes or more

3) in terminal of android studio, gradlew signingReport

4) get sha 1 and 256 from the signingreporrt add it to firepase in authentication

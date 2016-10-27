alias coff='sed -i "s/^/#/" ~/etc/cron.d/sakuli && omd reload crontab && cat ~/etc/cron.d/sakuli'
alias con='sed -i "s/^#//" ~/etc/cron.d/sakuli && omd reload crontab && cat ~/etc/cron.d/sakuli'
alias clist='cat ~/etc/cron.d/sakuli'

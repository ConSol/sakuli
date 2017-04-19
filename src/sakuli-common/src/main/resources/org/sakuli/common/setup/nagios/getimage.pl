#!/usr/bin/perl

use DBI;
use CGI;

my $cgi = CGI->new;

my $id = $cgi->param('id');
my $tbl = $cgi->param('tbl');
my $table;
if ($tbl eq "case") {
	$table = "sakuli_cases";
} elsif ($tbl eq "suite") {
	$table = "sakuli_suites";
}
my $database="sahi";
my $user="sakuli";
my $password="password";
my $hostname="xx.xx.xx.xx";
my $port=3306;

my $dsn="DBI:mysql:port=$port;database=$database;host=$hostname";
my $dbh=DBI->connect($dsn, $user, $password);
unless( $dbh ){
    print "Unable to connect to database";
        exit;
}
    my $sth=$dbh->prepare("select screenshot from $table where id='$id'");
    $sth->execute();
    my @data=$sth->fetchrow_array();
    print $cgi->header(
        #-type => 'image/jpg',
        -type => 'application/octet-stream',
	-attachment => "sakuli_screenshot_" . $tbl . "_id" . $id . ".jpg",
        -expires => 'now',
	#-content_length => length($data[0])
    );
    print $data[0];

    $sth->finish();
    $dbh->disconnect();

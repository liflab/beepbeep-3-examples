<?php

/* Replace all occurrences of {@docRoot} by an actual folder name
 * in all HTML files */
$base_dir = "docs/html/";
$doc_root = "doc-files";
$handle = opendir($base_dir);
while (false !== ($entry = readdir($handle)))
{
	if (is_dir($entry) || !preg_match("/\\.html$/", $entry))
		continue;
	echo $entry."\n";
	$contents = file_get_contents($base_dir.$entry);
	$contents = str_replace("{@docRoot}", $doc_root, $contents);
	file_put_contents($contents, $base_dir.$entry);

}
closedir($handle);

/* Copy all contents of Source/src/doc-files into docs/html/doc-files */
copy("Source/src/doc-files", "docs/html/doc-files");

?>

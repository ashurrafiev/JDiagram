set Inkscape="PATH_TO_INKSCAPE\Inkscape.exe"
%Inkscape% %1 --export-png="%~dpn1.png" --export-dpi=200
%Inkscape% %1 --export-pdf="%~dpn1.pdf"

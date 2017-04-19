#!/bin/bash
# http://askubuntu.com/a/90399
# Use this script if you have problems with non-unswapped memory. 
# * * * * * /bin/bash /usr/local/bin/toggle_swap.sh >> /tmp/toggle_swap.log


free_data="$(free)"
mem_data="$(echo "$free_data" | grep 'Mem:')"
free_mem="$(echo "$mem_data" | awk '{print $4}')"
buffers="$(echo "$mem_data" | awk '{print $6}')"
cache="$(echo "$mem_data" | awk '{print $7}')"
total_free=$((free_mem + buffers + cache))
used_swap="$(echo "$free_data" | grep 'Swap:' | awk '{print $3}')"

echo -e "Free memory:\t$total_free kB ($((total_free / 1024)) MB)\nUsed swap:\t$used_swap kB ($((used_swap / 1024)) MB)"
if [[ $used_swap -eq 0 ]]; then
    echo "Congratulations! No swap is in use."
elif [[ $used_swap -lt $total_free ]]; then
    echo "Freeing swap..."
    sudo swapoff -a
    sudo swapon -a
else
    echo "Not enough free memory. Exiting."
    exit 1
fi

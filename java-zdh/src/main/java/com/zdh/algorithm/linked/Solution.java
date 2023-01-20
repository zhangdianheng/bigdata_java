package com.zdh.algorithm.linked;

import com.zdh.algorithm.linked.node.ListNode;

/**
 * @author zdh
 * @date 2022-06-22 16:55
 * @Version 1.0
 */
public class Solution {

    /**
     * @Author zdh
     * @Description //TODO 重排链表--->给定链表 1->2->3->4->5, 重新排列为 1->5->2->4->3.
     * @Date 14:49 2022-06-23
     * @Param
     * @return
     **/
    //添加节点到我们的单向链表
    //思路：当不考虑顺序时
    //1.先找到链表的最后一个节点
    //2.将最后这个节点的next指向新的节点
    ListNode head = new ListNode(1,null);
    public void addNode(ListNode heroNode){
        //因为head节点不能动，我们需要一个辅助变量temp
        ListNode temp = head;
        //遍历链表，找到最后
        while(true){
            //找到最后一个节点
            if(temp.next == null){
                break;
            }
            //如果没有找到，则将temp后移
            temp = temp.next;
        }
        //当退出循环的时候，temp指向了链表最后的节点
        temp.next = heroNode;
    }
    //显示链表：通过一个辅助变量，帮助遍历整个单链表
    public ListNode showList(){
        //判断链表是否为空
        if(head.next == null){
            System.out.println("链表为空！");
            return null;
        }
        //因为头节点不能动，因此我们需要一个辅助变量来遍历
        System.out.println(head);
        return head;

    }


    public void reorderList(ListNode head) {
// a、寻找出原链表的中点，把链表划分为两个区域
        // b、将右边的链表进行反转
        // c、把这两个区域进行交错合并

        // 1、使用快慢指针寻找出链表的中点来
        // *******************************************************
        // 对于 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8
        // 中间节点值为 5
        // 所以左边区域为 1 -> 2 -> 3 -> 4 -> 5
        // 右边区域为 6 -> 7 -> 8
        // 但在视频讲解中，我把 5 归为了右边区域，这是一个错误
        // 虽然这个错误并不影响结果，因为合并过程都是一样的逻辑
        // *******************************************************
        ListNode mid = middleNode(head);

        // 2、基于 mid 这个中点，将链表划分为两个区域

        // 左边的区域开头节点是 head
        ListNode leftHead = head;

        // 右边的区域开头节点是 mid.next
        ListNode rightHead = mid.next;

        // 将链表断开，就形成了两个链表了
        mid.next = null;

        // 3、将右边的链表进行反转
        rightHead = reverseList(rightHead);

        // 4、将这两个链表进行合并操作，即进行【交错拼接】
        while (leftHead != null && rightHead != null) {

            // 拼接过程如下
            // 5、先记录左区域、右区域【接下来将有访问的两个节点】
            ListNode leftHeadNext = leftHead.next;

            ListNode rightHeadNext = rightHead.next;

            // 6、左边连接右边的开头
            leftHead.next = rightHead;

            // 7、leftHead 已经处理好，移动到下一个节点，即刚刚记录好的节点
            leftHead = leftHeadNext;

            // 8、右边连接左边的开头
            rightHead.next = leftHead;

            // 9、rightHead 已经处理好，移动到下一个节点，即刚刚记录好的节点
            rightHead = rightHeadNext;

        }

        System.out.println(head);
    }

    // LeetCode 876 : 链表的中间节点
    public ListNode middleNode(ListNode head) {

        ListNode fast = head;

        ListNode slow = head;

        while (fast != null && fast.next != null) {

            fast = fast.next.next;

            slow = slow.next;
        }

        return slow;

    }

    // LeetCode 206 : 反转链表
    public ListNode reverseList(ListNode head) {

        // 寻找递归终止条件
        // 1、head 指向的结点为 null
        // 2、head 指向的结点的下一个结点为 null
        // 在这两种情况下，反转之后的结果还是它自己本身
        if (head == null || head.next == null) return head;

        // 不断的通过递归调用，直到无法递归下去，递归的最小粒度是在最后一个节点
        // 因为到最后一个节点的时候，由于当前节点 head 的 next 节点是空，所以会直接返回 head
        ListNode cur = reverseList(head.next);

        // 比如原链表为 1 --> 2 --> 3 --> 4 --> 5
        // 第一次执行下面代码的时候，head 为 4，那么 head.next = 5
        // 那么 head.next.next 就是 5.next ，意思就是去设置 5 的下一个节点
        // 等号右侧为 head，意思就是设置 5 的下一个节点是 4

        // 这里出现了两个 next
        // 第一个 next 是「获取」 head 的下一节点
        // 第二个 next 是「设置」 当前节点的下一节点为等号右侧的值
        head.next.next = head;


        // head 原来的下一节点指向自己，所以 head 自己本身就不能再指向原来的下一节点了
        // 否则会发生无限循环
        head.next = null;

        // 我们把每次反转后的结果传递给上一层
        return cur;
    }


    /**
     * @Author zdh
     * @Description //TODO 链表排序
     * @Date 14:48 2022-06-23
     * @Param [head]
     * @return com.handday.algorithm.linked.node.ListNode
     **/

    public ListNode sortList(ListNode head) {

        // 获链表的总长度
        int length = 0;

        // 从链表的头节点开始访问
        ListNode node = head;

        // 利用 while 循环，可以统计出链表的节点个数，即长度
        while (node != null) {

            length++;

            node = node.next;

        }

        // 在原链表的头部设置一个虚拟头节点
        // 因为可能会操作到原链表的头节点
        // 设置了虚拟头节点后，原链表的头节点和原链表的其它节点地位一样
        ListNode dummyHead = new ListNode(0, head);

        // 利用 for 循环，执行合并的操作
        // 长度为 1 的链表和长度为 1 的链表合并后，形成一个长度为 2 的链表
        // 长度为 2 的链表和长度为 2 的链表合并后，形成一个长度为 4 的链表
        // 长度为 4 的链表和长度为 4 的链表合并后，形成一个长度为 8 的链表
        // 长度为 8 的链表和长度为 8 的链表合并后，形成一个长度为 16 的链表
        // 也有可能是，长度为 8 的链表和长度为 5 的链表合并后，形成一个长度为 13 的链表
        // 但是，每次合并过程中，子链表都会想要扩充为原来的两倍
        // 直到子链表想要扩充的长度超过了 length
        for (int subLength = 1; subLength < length; subLength *= 2) {

            // 整个归并过程分为三个步骤
            // 1、不停的划分，直到无法划分为止
            // 2、开始两两合并
            // 3、每次合并之后的结果都需要连接起来

            // 每次都把结果连接到 dummyHead，因此先记录一下
            // prev 表示已经排序好的链表的【尾节点】
            ListNode prev = dummyHead;

            // dummyHead 的后面节点才是原链表的节点，需要把它们进行划分
            // curr 表示所有正在准备排序的那些节点的【尾节点】
            ListNode curr = dummyHead.next;

            // 利用 while 循环，寻找出每次划分后子链表的头节点
            while (curr != null) {

                // 每次都是两个子链表开始合并

                // 1、先寻找出【左子链表】，长度为 subLength
                ListNode head1 = curr;

                // 通过 for 循环，找出 subLength 个节点来
                // curr 的索引为 0 ，需要再找 subLength - 1 个节点来
                for (int i = 1; i < subLength && curr.next != null; i++) {

                    curr = curr.next;

                }

                // 2、再寻找出【右子链表】，长度最多为 subLength，甚至有可能长度为 0
                ListNode head2 = curr.next;

                // 此时，需要将【左子链表】与【右子链表】的连接断开
                curr.next = null;

                // curr 来到【右子链表】的头部
                curr = head2;

                // 通过 for 循环，找出【右子链表】的那些节点来
                // 【右子链表】的节点个数可能达不到 subLength，甚至只有 1 个或者 0 个节点
                for (int i = 1; i < subLength && curr != null && curr.next != null; i++) {

                    curr = curr.next;

                }

                // 获取到【右子链表】之后，需要把它和后续链表断开
                // next 表示接下来需要排序的那些节点的【首节点】
                ListNode next = null;

                // 如果 curr != null，那么说明【右子链表】的节点个数达到了 subLength 个，并且后续还有节点
                if (curr != null) {

                    // 记录一下后面节点
                    next = curr.next;

                    // 再将【右子链表】和后续链表断开
                    curr.next = null;

                }

                // 将【左子链表】与【右子链表】合并
                ListNode merged = mergeTwoLists(head1, head2);

                // 合并之后的结果需要连接到前一个链表
                prev.next = merged;

                // prev 来到链表的尾部，是下一个即将合成链表之后的前一个链表的尾节点
                while (prev.next != null) {

                    prev = prev.next;

                }

                // curr 来到 next，处理后面的节点
                curr = next;
            }

        }

        return dummyHead.next;
    }

    // 合并两个有序链表的代码
    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        // 一开始设置一个虚拟节点，它的值为 -1，它的值可以设置为任何的数，因为我们根本不需要使用它的值
        ListNode dummy = new ListNode(-1);

        // 设置一个指针，指向虚拟节点
        ListNode pre = dummy;

        // 通过一个循环，不断的比较 l1 和 l2 中当前节点值的大小，直到 l1 或者 l2 遍历完毕为止
        while (l1 != null && l2 != null) {
            // 如果 l1 当前节点的值小于等于了 l2 当前节点的值
            if (l1.val <= l2.val) {
                // 让 pre 指向节点的 next 指针指向这个更小值的节点
                // 即指向 l1
                pre.next = l1;
                // 让 l1 向后移动
                l1 = l1.next;
            }else {
                // 让 pre 指向节点的 next 指针指向这个更小值的节点
                // 即指向 l2
                pre.next =l2;
                // 让 l2 向后移动
                l2 = l2.next;
            }
            // 让 pre 向后移动
            pre = pre.next;
        }

        // 跳出循环后，l1 或者 l2 中可能有剩余的节点没有被观察过
        // 直接把剩下的节点加入到 pre 的 next 指针位置

        // 如果 l1 中还有节点
        if (l1 != null) {
            // 把 l1 中剩下的节点全部加入到 pre 的 next 指针位置
            pre.next = l1;
        }

        // 如果 l2 中还有节点
        if (l2 != null) {
            // 把 l2 中剩下的节点全部加入到 pre 的 next 指针位置
            pre.next = l2;
        }

        // 最后返回虚拟节点的 next 指针
        return dummy.next;
    }

    /**
     * @Author zdh
     * @Description //TODO 旋转链表
     * @Date 15:24 2022-06-23
     * @Param
     * @return
     **/
    public ListNode rotateRight(ListNode head, int k) {

        // 边界条件判断
        if( head == null)  {
            return head;
        }

        // 1、第一步先要计算出链表的长度来
        int len = 0;

        // 2、这里我们设置一个指针指向链表的头节点的位置
        ListNode tempHead = head;

        // 3、通过不断的移动 tempHead ，来获取链表的长度
        while (tempHead != null) {

            // tempHead 不断向后移动，直到为 null
            tempHead = tempHead.next;

            // 每次遍历一个新的节点，意味着链表长度新增 1
            len++;

        }

        // 由于题目中的 k 会超过链表的长度，因此进行一个取余的操作
        // 比如 k = 1000，len = 999
        // 实际上就是将链表每个节点向右移动 1000 % 999 = 1 个位置就行了
        // 因为链表中的每个节点移动 len 次会回到原位
        k = k % len;


        // 4、接下来设置两个指针指向链表的头节点
        // 这两个指针的目的是去寻找出旋转之前的尾节点位置、旋转成功之后的尾节点位置

        // former 指针
        ListNode former = head;

        // latter 指针
        ListNode latter = head;

        // former 指针先向前移动 k 次
        for(int i = 0 ; i < k ; i++){

            // former 不断向后移动
            former = former.next;

        }

        // 这样 former 和 latter 就相差了 k 个节点
        // 5、接下来，两个指针同时向后移动，直到 former 来到了最后一个节点位置
        while(former.next != null){

            // former 不断向后移动
            former = former.next;

            // latter 不断向后移动
            latter = latter.next;
        }

        // 6、此时，former 指向了最后一个节点，需要将这个节点和原链表的头节点连接起来
        former.next = head;

        // 7、latter 指向的节点的【下一个节点】是倒数第 k 个节点，那就是旋转成功之后的头节点
        ListNode newHead = latter.next;

        // 8、断开链接，避免成环
        latter.next = null;

        // 9、返回 newHead
        return newHead;

    }

}
